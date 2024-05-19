package io.github.homchom.recode.mod.features.commands.nbs;

import io.github.homchom.recode.mod.features.commands.nbs.exceptions.OutdatedNBSException;
import io.github.homchom.recode.sys.player.chat.ChatUtil;

import java.io.*;
import java.math.BigDecimal;

import static io.github.homchom.recode.RecodeKt.logDebug;

// Credit to https://github.com/koca2000/NoteBlockAPI/blob/master/src/main/java/com/xxmicloxx/NoteBlockAPI/NBSDecoder.java
public class NBSDecoder {

    public static SongData parse(File songFile) throws IOException, OutdatedNBSException {
        try {
            return parse(new FileInputStream(songFile), songFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SongData parse(InputStream inputStream, File songFile) throws IOException, OutdatedNBSException {
        String title = "";
        String author = "";
        String file = songFile.getName();
        float speed = 0f;
        float actualSpeed = 0f;
        short timeSignature = 4;
        int loopTick = 0;
        int loopCount = 0;
        int vanillaInstruments = 9;

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder layerStringBuilder = new StringBuilder();

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        short length = readShort(dataInputStream);
        int nbsversion = 0;
        if (length == 0) {
            nbsversion = dataInputStream.readByte();
            vanillaInstruments = dataInputStream.readByte();
            if (nbsversion >= 3) {
                length = readShort(dataInputStream);
            } else if (nbsversion == 1 || nbsversion == 2) {
                throw new OutdatedNBSException();
            }
        } else {
            ChatUtil.sendMessage("§a§lHEY!§r Looks like you are using original Note Block Studio. I recommend you to use §bOpen Note Block Studio§r, which is an unofficial continuation of Note Block Studio!");
        }
        short layers = readShort(dataInputStream); //song height
        title = readString(dataInputStream); //title
        author = readString(dataInputStream); //author
        readString(dataInputStream); //original author
        String description = readString(dataInputStream); //description
        actualSpeed = readShort(dataInputStream); //speed
        speed = actualSpeed / 100f; //speed
        dataInputStream.readBoolean(); //autosave
        dataInputStream.readByte(); //autosave duration
        timeSignature = dataInputStream.readByte(); //time signature
        readInt(dataInputStream); //minutes spent
        readInt(dataInputStream); //left clicks
        readInt(dataInputStream); //right clicks
        readInt(dataInputStream); //blocks added
        readInt(dataInputStream); //nlocks removed
        readString(dataInputStream); // mid or schematic filename
        if (nbsversion >= 4) {
            dataInputStream.readByte(); //loop on/off
            loopCount = dataInputStream.readByte(); //loop count
            loopTick = readShort(dataInputStream); //loop start tick
        }

        short tick = -1;
        String[][] addStringList = new String[layers][length + 1];
        int[][] instrumentList = new int[layers][length + 1];
        int[][] pitchList = new int[layers][length + 1];
        int[][] finepitchList = new int[layers][length + 1];
        int[][] velocityList = new int[layers][length + 1];
        int[][] panningList = new int[layers][length + 1];
        boolean[] columnExistence = new boolean[length + 1];
        boolean[][] noteExistence = new boolean[layers][length + 1];
        boolean firstNoted = false;

        while (true) { //Read notes
            short t = readShort(dataInputStream);
            if (t == 0) {
                break;
            }
            tick += t;

            columnExistence[tick] = true;

            short layer = -1;
            while (true) {
                short jumpLayers = readShort(dataInputStream);
                if (jumpLayers == 0) {
                    break;
                }
                layer += jumpLayers;
                byte instrument = dataInputStream.readByte();
                byte note = dataInputStream.readByte();
                byte velocity = 100;
                int panning = 100;
                short finepitch = 0;
                if (nbsversion >= 4) {
                    velocity = dataInputStream.readByte();
                    panning = Byte.toUnsignedInt(dataInputStream.readByte());
                    finepitch = readShort(dataInputStream);
                }

                instrumentList[layer][tick] = instrument;
                pitchList[layer][tick] = note;
                finepitchList[layer][tick] = finepitch;
                velocityList[layer][tick] = velocity;
                panningList[layer][tick] = panning;
                noteExistence[layer][tick] = true;
            }
        }

        for (int i = 0; i < layers; i++) { //Read layer data

            String name = readString(dataInputStream);

            if (nbsversion >= 4) {
                dataInputStream.readByte();
            }

            byte volume = dataInputStream.readByte();
            int panning = 100;

            if (nbsversion >= 2) {
                panning = Byte.toUnsignedInt(dataInputStream.readByte());
            }

            for (int currentTick = 0; currentTick < length + 1; currentTick++) {
                boolean noteExists = noteExistence[i][currentTick];
                if (noteExists) {

                    int noteVelocity = velocityList[i][currentTick];
                    int notePanning = panningList[i][currentTick];

                    double averageVelocity = noteVelocity * (volume / 100d);
                    double averagePanning = (notePanning + panning) / 2d;

                    double preFinalPanning = (averagePanning - 100) / 50;

                    String finalVelocity = new BigDecimal(averageVelocity).setScale(3, BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
                    String finalPanning = new BigDecimal(preFinalPanning).setScale(3, BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();

                    String finalString;
                    if (preFinalPanning == 0) {
                        finalString = "," + finalVelocity;
                    } else {
                        finalString = "," + finalVelocity + "," + finalPanning;
                    }
                    addStringList[i][currentTick] = finalString;
                }
            }

            String finalLayerVolume = new BigDecimal(volume).setScale(3, BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
            String finalLayerPanning = new BigDecimal(panning).setScale(3, BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();

            layerStringBuilder.append("=").append(finalLayerVolume).append(",").append(finalLayerPanning);
        }

        int customInstruments = 0;
        customInstruments = dataInputStream.readByte();

        int[] customPitchList = new int[customInstruments];
        String[] customNameList = new String[customInstruments];

        if (customInstruments >= 1) {
            for (int i = 0; i < customInstruments; i++) {
                int instrumentOffset = vanillaInstruments + customInstruments;
                int instrumentPitch = 0;

                customNameList[i] = readString(dataInputStream); //Instrument name
                readString(dataInputStream); //Sound file

                instrumentPitch = dataInputStream.readByte(); //Sound pitch

                customPitchList[i] = instrumentPitch;


                dataInputStream.readByte();    //Press key
            }
        }

        dataInputStream.close();

        for (int currentTick = 0; currentTick < length + 1; currentTick++) {
            boolean columnExists = columnExistence[currentTick];
            if (columnExists) {
                StringBuilder columnStringBuilder = new StringBuilder();
                if (!firstNoted) {
                    columnStringBuilder.append(currentTick + 1);
                    firstNoted = true;
                } else {
                    columnStringBuilder.append("=").append(currentTick + 1);
                }
                boolean firstAppend = true;
                for (int i = 0; i < layers; i++) {
                    boolean noteExists = noteExistence[i][currentTick];
                    if (noteExists) {
                        String laterNoteString = addStringList[i][currentTick];

                        int noteInstrument = instrumentList[i][currentTick];
                        int noteKey = pitchList[i][currentTick];
                        int noteFinePitch = finepitchList[i][currentTick];
                        int noteKeyOffset = 0;


                        if (noteInstrument >= vanillaInstruments) {
                            int instrumentId = noteInstrument - vanillaInstruments;
                            noteKeyOffset = customPitchList[instrumentId] - 45;
                        }
                        if (firstAppend) {
                            columnStringBuilder.append(":").append(noteInstrument + 1).append(",").append(getMinecraftPitch(noteKey + (double) noteFinePitch / 100d, noteKeyOffset)).append(laterNoteString);
                            firstAppend = false;
                        } else {
                            columnStringBuilder.append(";").append(noteInstrument + 1).append(",").append(getMinecraftPitch(noteKey + (double) noteFinePitch / 100d, noteKeyOffset)).append(laterNoteString);
                        }
                    }
                }
                stringBuilder.append(columnStringBuilder);
            }
        }


        return new SongData(title, author, speed, (int) Math.ceil((length + 1.0) / (4 * timeSignature)) * (4 * timeSignature), stringBuilder.toString(), file, layerStringBuilder.toString(), (loopTick + 1), loopCount, customInstruments, customNameList);
    }

    private static short readShort(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dataInputStream) throws IOException {
        int byte1 = dataInputStream.readUnsignedByte();
        int byte2 = dataInputStream.readUnsignedByte();
        int byte3 = dataInputStream.readUnsignedByte();
        int byte4 = dataInputStream.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dataInputStream) throws IOException {
        int length = readInt(dataInputStream);
        StringBuilder builder = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dataInputStream.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private static int getMinecraftPitch(double key, double offset) {

        if (key < 33) key -= 9;
        else if (key > 57) key -= 57;
        else key -= 33;

        key += offset;

        double finalValue = (0.5 * (Math.pow(2, (key / 12)))) * 1000;

        return (int) finalValue;
    }

    public SongData parse(InputStream inputStream) throws IOException, OutdatedNBSException {
        return parse(inputStream, null);
    }
}
