package me.reasonless.codeutilities.nbs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import me.reasonless.codeutilities.nbs.exception.OutdatedNBSException;

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

	public SongData parse(InputStream inputStream) throws IOException, OutdatedNBSException {
		return parse(inputStream, null);
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
		int vanillaInstruments = 0;

		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder layerStringBuilder = new StringBuilder();

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        short length = readShort(dataInputStream);
        int nbsversion = 0;
        nbsversion = dataInputStream.readByte();

        if(nbsversion != 4) {
            throw new OutdatedNBSException();
        }
        vanillaInstruments = dataInputStream.readByte();
        if (length == 0) {
            length = readShort(dataInputStream);
        }
        short layers = readShort(dataInputStream);
        title = readString(dataInputStream);
        author = readString(dataInputStream);
        readString(dataInputStream);
        String description = readString(dataInputStream);
        actualSpeed = readShort(dataInputStream);
        speed = actualSpeed / 100f;
        dataInputStream.readBoolean();
        dataInputStream.readByte();
        timeSignature = (short) dataInputStream.readByte();
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readString(dataInputStream);
        dataInputStream.readByte();
        loopCount = dataInputStream.readByte();
        loopTick = readShort(dataInputStream);
        short tick = -1;
        String[][] stringList = new String[layers][length + 1];
        String[][] addStringList = new String[layers][length + 1];
        int[][] instrumentList = new int[layers][length + 1];
        int[][] pitchList = new int[layers][length + 1];
        int[][] finepitchList = new int[layers][length + 1];
        int[][] velocityList = new int[layers][length + 1];
        int[][] panningList = new int[layers][length + 1];
        while (true) { //Read notes
            short t = readShort(dataInputStream);
            if (t == 0) {
                break;
            }
            tick += t;

            short layer = -1;
            while (true) {
                short jumpLayers = readShort(dataInputStream);
                if (jumpLayers == 0) {
                    break;
                }
                layer += jumpLayers;
                byte instrument = dataInputStream.readByte();
                byte note = dataInputStream.readByte();
                byte velocity = dataInputStream.readByte();
                int panning = Byte.toUnsignedInt(dataInputStream.readByte());
                short finepitch = readShort(dataInputStream);
                
                stringList[layer][tick] = "=" + (instrument + 1) + ";" + (tick + 1);
                instrumentList[layer][tick] = instrument;
                pitchList[layer][tick] = note;
                finepitchList[layer][tick] = finepitch;
                velocityList[layer][tick] = velocity;
                panningList[layer][tick] = panning;
            }
        }

        for (int i = 0; i < layers; i++) { //Read layer data

            String name = readString(dataInputStream);
            dataInputStream.readByte();


            byte volume = dataInputStream.readByte();
            int panning = Byte.toUnsignedInt(dataInputStream.readByte());
            
            for(int currentTick = 0; currentTick < length + 1; currentTick++) {
            	String noteString = stringList[i][currentTick];
            	if (noteString != null) {
            		
            		int noteVelocity = velocityList[i][currentTick];
                	int notePanning = panningList[i][currentTick];
                	
                	double averageVelocity = noteVelocity * (volume/100d);
                	double averagePanning = (notePanning + panning)/2d;
                	
                	String finalVelocity = new BigDecimal(averageVelocity).setScale(3,BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
                	String finalPanning = new BigDecimal((averagePanning - 100)/50).setScale(3,BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();

                	String finalString = ";" + finalVelocity + ";" + finalPanning;
                	addStringList[i][currentTick] = finalString;
            	}
            }
            
            String finalLayerVolume = new BigDecimal(volume).setScale(3,BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
            String finalLayerPanning = new BigDecimal(panning).setScale(3,BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
            
            layerStringBuilder.append("=" + finalLayerVolume + ";" + finalLayerPanning);
        }
        
        int customInstruments = 0;
        customInstruments = dataInputStream.readByte();
        
        int[] customPitchList = new int[customInstruments];
        
        if (customInstruments >= 1) {
        	for (int i = 0; i < customInstruments; i++) {
        		int instrumentOffset = vanillaInstruments + customInstruments;
        		int instrumentPitch = 0;
        		
        		readString(dataInputStream); //Instrument name
        		readString(dataInputStream); //Sound file
        		
        		instrumentPitch = dataInputStream.readByte(); //Sound pitch
        		
        		customPitchList[i] = instrumentPitch;
        		
        		dataInputStream.readByte();	//Press key
        	}
        }
        
        for (int i = 0; i < layers; i++) {
        	for(int currentTick = 0; currentTick < length + 1; currentTick++) {
        		String noteString = stringList[i][currentTick];
        		if (noteString != null) {
        			String laterNoteString = addStringList[i][currentTick];
                 		
        			int noteInstrument = instrumentList[i][currentTick];
        			int noteKey = pitchList[i][currentTick];
        			int noteFinePitch = finepitchList[i][currentTick];
                
        			if (noteInstrument > vanillaInstruments) {
        				int instrumentId = noteInstrument - vanillaInstruments;
        				int noteKeyOffset = customPitchList[instrumentId];

        				noteKey += (noteKey - noteKeyOffset); 
        			}
        			String finalString = noteString + ";" + getMinecraftPitch(noteKey + (double)noteFinePitch/100d) + laterNoteString;
        			stringBuilder.append(finalString);
        		}
        	}
        }

        dataInputStream.close();
        
        //System.out.println(stringBuilder.toString()); //COOL DEBUG CODE
        //System.out.println(layerStringBuilder.toString()); //COOL DEBUG CODE
        
        return new SongData(title, author, speed, (int)((Math.ceil((length+1) / timeSignature) + 1) * timeSignature), stringBuilder.toString(), file, layerStringBuilder.toString(), (loopTick + 1), loopCount);
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
	
	private static String getMinecraftPitch(double key) {
		
		if (key < 33) key -= 9;
		else if (key > 57) key -= 57;
		else key -= 33;
		
		BigDecimal pitch = new BigDecimal(0.5 * (Math.pow(2,(key/12))));
		
		return pitch.setScale(3,BigDecimal.ROUND_FLOOR).stripTrailingZeros().toPlainString();
	}
}
