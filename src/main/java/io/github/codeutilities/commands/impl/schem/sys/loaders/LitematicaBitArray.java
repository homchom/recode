package io.github.codeutilities.commands.impl.schem.sys.loaders;

public class LitematicaBitArray {
    /**
     * The long array that is used to store the data for this BitArray.
     */
    private final long[] longArray;
    /**
     * Number of bits a single entry takes up
     */
    private final int bitsPerEntry;
    /**
     * The maximum value for a single entry. This also works as a bitmask for a single entry.
     * For instance, if bitsPerEntry were 5, this value would be 31 (ie, {@code 0b00011111}).
     */
    private final long maxEntryValue;
    /**
     * Number of entries in this array (<b>not</b> the length of the long array that internally backs this array)
     */
    private final long arraySize;

    public LitematicaBitArray(int bitsPerEntryIn, long arraySizeIn) {
        this(bitsPerEntryIn, arraySizeIn, null);
    }

    public LitematicaBitArray(int bitsPerEntryIn, long arraySizeIn, long[] longArrayIn) {
        isInRange(1L, 32L, bitsPerEntryIn);
        this.arraySize = arraySizeIn;
        this.bitsPerEntry = bitsPerEntryIn;
        this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;

        if (longArrayIn != null) {
            this.longArray = longArrayIn;
        } else {
            this.longArray = new long[(int) (Math.ceil(arraySizeIn * (long) bitsPerEntryIn) / 64L)];
        }
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public void setAt(long index, int value) {
        isInRange(0L, this.arraySize - 1L, index);
        isInRange(0L, this.maxEntryValue, value);
        long startOffset = index * (long) this.bitsPerEntry;
        int startArrIndex = (int) (startOffset >> 6); // startOffset / 64
        int endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
        int startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64
        this.longArray[startArrIndex] = this.longArray[startArrIndex] & ~(this.maxEntryValue << startBitOffset) | ((long) value & this.maxEntryValue) << startBitOffset;

        if (startArrIndex != endArrIndex) {
            int endOffset = 64 - startBitOffset;
            int j1 = this.bitsPerEntry - endOffset;
            this.longArray[endArrIndex] = this.longArray[endArrIndex] >>> j1 << j1 | ((long) value & this.maxEntryValue) >> endOffset;
        }
    }

    public int getAt(long index) {
        isInRange(0L, this.arraySize - 1L, index);

        long startOffset = index * (long) this.bitsPerEntry;
        int startArrIndex = (int) (startOffset >> 6); // startOffset / 64
        int endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
        int startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64

        if (startArrIndex >= this.longArray.length) {
            startArrIndex = clamp(startArrIndex, 0, (this.longArray.length - 1));
        }
        if (endArrIndex >= this.longArray.length) {
            endArrIndex = clamp(endArrIndex, 0, (this.longArray.length - 1));
        }
        if (startArrIndex == endArrIndex) {
            return (int) (this.longArray[startArrIndex] >>> startBitOffset & this.maxEntryValue);
        } else {
            int endOffset = 64 - startBitOffset;
            return (int) ((this.longArray[startArrIndex] >>> startBitOffset | this.longArray[endArrIndex] << endOffset) & this.maxEntryValue);
        }
    }

    public int[] getValueCounts() {
        int[] counts = new int[(int) this.maxEntryValue + 1];

        for (int i = 0; i < this.arraySize; ++i) {
            ++counts[this.getAt(i)];
        }

        return counts;
    }

    public long[] getBackingLongArray() {
        return this.longArray;
    }

    public long size() {
        return this.arraySize;
    }

    public void isInRange(long start, long end, long value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(value + " not in the range of " + start + " to " + end);
        }
    }
}