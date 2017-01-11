package com.test.utils.levenshtein;

/**
 * Created by admin on 2016/9/13.
 */
public class Levenshtein {

    public static void main(String[] args) {
        String s = "goo12";
        String t = "goo13";
        System.out.println(distance(s, t));
        //相似度
        System.out.println(1 - distance(s, t) * 1.0 / Math.max(s.length(), t.length()));
    }

    /**
     * 2个字符的距离，用于计算相似度
     *
     * @param source
     * @param target
     * @return
     */
    public static Integer distance(String source, String target) {
        int[] v0 = new int[target.length() + 1];
        int[] v1 = new int[target.length() + 1];

        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }
        for (int i = 0; i < source.length(); i++) {
            v1[0] = i + 1;
            for (int j = 0; j < target.length(); j++) {
                int coast = source.charAt(i) == target.charAt(j) ? 0 : 1;
                v1[j + 1] = min(v0[j] + coast, v1[j] + 1, v0[j + 1] + 1);
            }

            int[] h = v0;
            v0 = v1;
            v1 = new int[target.length() + 1];
        }
        return v0[target.length()];
    }

    private static int min(int i, int j, int k) {
        return Math.min(Math.min(i, j), k);
    }
}
