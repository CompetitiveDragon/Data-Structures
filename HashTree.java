
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.util.Collections.reverse;

public final class HashTree implements Serializable {
    private final String mHashAlgorithm; // e.g. "SHA-1" "SHA-256" "MD5"
    private final List<byte[]> mData;
    private final int mNarity;
    private final List<List<byte[]>> mDigestTree = new ArrayList<>();

    public HashTree(String hashAlgorithm) {
        this(hashAlgorithm, 2);
    }

    public HashTree(String hashAlgorithm, int narity) {
        narity = max(2, narity);
        mNarity = narity;
        mHashAlgorithm = hashAlgorithm;
        mData = new ArrayList<>();
    }

    private HashTree(int narity, String hashAlgorithm, List<byte[]> existingData, byte[] newData) {
        mNarity = narity;
        mHashAlgorithm = hashAlgorithm;
        mData = deepCopy(existingData);
        mData.add(newData);
        try {
            computeDigestTree();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public HashTree append(byte[] data) {
        return new HashTree(mNarity, mHashAlgorithm, mData, data);
    }

    public int size() {
        return mData.size();
    }

    public int getNarity() {
        return mNarity;
    }

    public byte[] getRootDigest() {
        if (this.size() != 0) {
            return mDigestTree.get(mDigestTree.size() - 1).get(0);
        }
        return null;
    }

    public List<byte[]> getPathToLeaf(int id) {
        if (id >= this.size() || id < 0) {
            return  null;
        }
        List<byte[]> res = new ArrayList<>();
        for (List<byte[]> layer : mDigestTree) {
            res.add(layer.get(id));
            id /= mNarity;
        }
        reverse(res);
        return res;
    }

    public boolean checkIfPathToLeafExists(List<byte[]> path) {
        if (mDigestTree.size() != path.size()) {
            return false;
        }
        List<Integer> indexesToCheck = new ArrayList<>();
        indexesToCheck.add(0);
        for (int layerIndex = mDigestTree.size() - 1; layerIndex >= 0 && !indexesToCheck.isEmpty(); layerIndex--) {
            List<Integer> childrenIndexes = new ArrayList<>();
            List<byte[]> currentLayer = mDigestTree.get(layerIndex);
            for (int item : indexesToCheck) {
                if (item >= currentLayer.size()) {
                    continue;
                }
                if (Arrays.equals(path.get(path.size() - layerIndex - 1), currentLayer.get(item))) {
                    for (int i = 0; i < mNarity; i++) {
                        childrenIndexes.add(item * mNarity + i);
                    }
                }
            }
            indexesToCheck = childrenIndexes;
        }
        return !indexesToCheck.isEmpty();
    }

    public List<Integer> getDifferentLeaves(HashTree other) {
        if (other == null || this.size() == 0 ||this.size() != other.size() || this.getNarity() != other.getNarity()) {
            throw new IllegalArgumentException("A non-null tree with same size and N-arity is required1");
        }
        List<Integer> indexesToCheck = new ArrayList<>();
        indexesToCheck.add(0);
        for (int layerIndex = mDigestTree.size() - 1; layerIndex >= 1 && !indexesToCheck.isEmpty(); layerIndex--) {
            List<Integer> childrenIndexes = new ArrayList<>();
            List<byte[]> currentLayer = mDigestTree.get(layerIndex);
            for (int item : indexesToCheck) {
                if (item >= currentLayer.size()) {
                    continue;
                }
                if (!Arrays.equals(currentLayer.get(item), other.getHashAtPosition(layerIndex, item))) {
                    for (int i = 0; i < mNarity; i++) {
                        childrenIndexes.add(item * mNarity + i);
                    }
                }
            }
            indexesToCheck = childrenIndexes;
        }
        List<Integer> result = new ArrayList<>();
        for (int index : indexesToCheck) {
            if (index >= mDigestTree.get(0).size()) {
                continue;
            }
            if (!Arrays.equals(this.getHashAtPosition(0, index), other.getHashAtPosition(0, index))) {
                result.add(index);
            }
        }
        return result;
    }

    protected byte[] getHashAtPosition(int layer, int index) {
        if (layer < 0 || layer > this.size() || index < 0 || mDigestTree.get(layer).size() <= index) {
            return null;
        }
        return mDigestTree.get(layer).get(index).clone();
    }

    private static List<byte[]> deepCopy(List<byte[]> list) {
        List<byte[]> result = new ArrayList<>();
        for (byte[] item : list)
            result.add(item.clone());
        return result;
    }

    private void computeDigestTree() throws NoSuchAlgorithmException {
        mDigestTree.clear();
        ArrayList<byte[]> firstLayer = new ArrayList<>();
        mDigestTree.add(firstLayer);
        for (byte[] data : mData) {
            firstLayer.add(hash(data));
        }
        List<byte[]> prevLayer = mDigestTree.get(mDigestTree.size() - 1);
        while (prevLayer.size() > 1) {
            List<byte[]> newLayer = new ArrayList<>();
            int index;
            for (index = 0; index + mNarity <= prevLayer.size(); index += mNarity) {
                List<byte[]> newNode = new ArrayList<>();
                for (int i = 0; i < mNarity; i++) {
                    newNode.add(prevLayer.get(index + i));
                }
                newLayer.add(hash(newNode));
            }
            if (index != prevLayer.size()) {
                List<byte[]> newNode = new ArrayList<>();
                for (int i = 0; i < mNarity; i++) {
                    newNode.add(prevLayer.get(min(index + i, prevLayer.size() - 1)));
                }
                newLayer.add(hash(newNode));
            }
            prevLayer = newLayer;
            mDigestTree.add(newLayer);
        }
    }

    private byte[] hash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mHashAlgorithm);
        messageDigest.update(input);
        return messageDigest.digest();
    }

    private byte[] hash(List<byte[]> input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mHashAlgorithm);
        for (byte[] item : input) {
            messageDigest.update(item);
        }
        return messageDigest.digest();
    }
}
