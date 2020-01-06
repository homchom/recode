package me.reasonless.codeutilities.nbs;

import me.reasonless.codeutilities.nbs.enums.Instrument;
import me.reasonless.codeutilities.nbs.enums.Pitch;
import me.reasonless.codeutilities.nbs.exception.OutdatedNBSException;

import java.io.*;

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

		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder layerStringBuilder = new StringBuilder();

        DataInputStream dataInputStream = new DataInputStream(inputStream);
        short length = readShort(dataInputStream);
        int nbsversion = 0;
        nbsversion = dataInputStream.readByte();

        if(nbsversion != 4) {
            throw new OutdatedNBSException();
        }
        if (length == 0) {
            dataInputStream.readByte();
            readShort(dataInputStream);
        }
        short layers = readShort(dataInputStream);
        title = readString(dataInputStream);
        author = readString(dataInputStream);
        readString(dataInputStream);
        String description = readString(dataInputStream);
        speed = readShort(dataInputStream) / 100f;
        dataInputStream.readBoolean();
        dataInputStream.readByte();
        dataInputStream.readByte();
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readInt(dataInputStream);
        readString(dataInputStream);
        dataInputStream.readByte();
        dataInputStream.readByte();
        readShort(dataInputStream);
        short tick = -1;
        while (true) {
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
                byte panning = dataInputStream.readByte();
                short finepitch = readShort(dataInputStream);

                stringBuilder.append("=" + Instrument.values()[instrument].getDF() + ";" + (tick + 1) + ";" + Pitch.getNBSPitch(note).getDFPitch() + ";" + velocity + ";" + panning + ";" + finepitch);
            }
        }

        for (int i = 0; i < layers; i++) {

            String name = readString(dataInputStream);
            dataInputStream.readByte();


            byte volume = dataInputStream.readByte();
            byte panning = dataInputStream.readByte();

            layerStringBuilder.append("=" + volume + ";" + panning);
        }



        System.out.println(title + ";" + author + ";" + description + ";" + speed);
        dataInputStream.close();
			

        return new SongData(title, author, speed, stringBuilder.toString(), file, layerStringBuilder.toString());
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

}
