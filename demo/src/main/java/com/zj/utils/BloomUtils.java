package com.zj.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.List;

public class BloomUtils {

    public static BloomFilter<String> getBloomFilter(List<String> list)
    {
        BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),100,0.001);
        for (String s:list)
        {
            filter.put(s);
        }
        return filter;
    }
}
