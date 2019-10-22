package com.demo.xstart;

import com.demo.model.Blog;
import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;
import com.jfinal.config.Constants;
import com.jfinal.core.Action;
import com.jfinal.core.JFinal;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;


public class BlogService {
    static Log log = Log.getLog(BlogService.class);

    public Blog blogDao = new Blog().dao();

    public void showlog() {
        log.info("ahhahah");
    }

    public void dumpInof() {
        JFinal me = JFinal.me();

        List<String> keys = me.getAllActionKeys();
        for(String k:keys) {
            String[] urlPara = null;
            Action action = me.getAction(k, urlPara);
            String msg = String.format("controlKey=%s,\t\t actionKey=%s, \t\t methodName=%s.%s",
                action.getControllerKey(), action.getActionKey(),
                action.getControllerClass().getName(), action.getMethodName());
            System.out.println(msg);
        }

        Constants cons = me.getConstants();
        System.out.println(JsonKit.toJson(cons));
    }

    public void localFile() {
        String loc = PathKit.getWebRootPath();

        String fname = loc + "/zdata/TDA.txt";
        System.out.println("location: " + loc);

        File testFile = new File(fname);

        try{
            List<String> lines = Files.readLines(testFile, Charsets.UTF_8);
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cpath = PathKit.getRootClassPath();
        System.out.println("class path: " + cpath);


    }

    void precondition() {
        int i = 1;
        String a = "";
        int[] j = {4, 1, 2, 3};
        List l = Ints.asList(j);

        //前置检查方法
        //条件检查 不通过抛出IllegalArgumentException及自定义描述 自定义描述支持类似于String.format()的字符串拼接但是只能用%s
        checkArgument(i >= 0, "Argument was %s but expected nonnegative", i);
        //空值检查 不通过抛出NullPointerException及自定义描述
        checkNotNull(a, "Argument was null");
        //状态检查 不通过抛出IllegalStateException及自定义描述
        checkState(i >= 0, "Argument was %s but expected nonnegative", i);
        //检查列表、字符串或数组某一索引是否有效 不通过抛出IndexOutOfBoundsException
        checkElementIndex(2, l.size());
        //检查列表、字符串或数组某一位置是否有效 不通过抛出IndexOutOfBoundsException
        checkPositionIndex(2, l.size());
        //检查列表、字符串或数组某一范围是否有效 不通过抛出IndexOutOfBoundsException
        checkPositionIndexes(1, 2, l.size());
    }

    void ordering() {
        int[] j = {4, 1, 2, 3};
        List l = Ints.asList(j);

        //对可排序类型做自然排序，如数字按大小，日期按先后排序
        List sortl = Ordering.natural().sortedCopy(l);

        System.out.println(sortl);

        //以下是具体排序方法
        //获取可迭代对象中最大的k个元素。
        List greatestOf = Ordering.natural().greatestOf(l, 3);
        System.out.println(greatestOf);

        //判断可迭代对象是否已按排序器排序：允许有排序值相等的元素。
        System.out.println(Ordering.natural().isOrdered(l));

        //返回两个参数中最小的那个。如果相等，则返回第一个参数。
        Ordering.natural().min(1, 2);
        //返回多个参数中最小的那个。如果有超过一个参数都最小，则返回第一个最小的参数。
        Ordering.natural().min(1, 2, 3, 4);
        //返回迭代器中最小的元素。如果可迭代对象中没有元素，则抛出NoSuchElementException。
        Ordering.natural().min(l);
        //同理max
        Ordering.natural().max(1, 2);
    }

    void immutable() {
        ImmutableSet<String> foob = ImmutableSet.of("a", "b", "c");
        System.out.println(foob.contains("a"));

        int[] j = {4, 1, 2, 3};
        List l = Ints.asList(j);
        ImmutableSet<Integer> defensiveCopy = ImmutableSet.copyOf(l);

        List k = l;

        l.set(2, -9);
        System.out.println(k);
        System.out.println(defensiveCopy);

        //其对应的实现HashBiMap，ImmutableBiMap，EnumBiMap，EnumHashBiMap
        BiMap<Integer, String> userId = HashBiMap.create();
        userId.put(1, "tom");
        //在想使用反向映射时使用inverse反转键值对关系
        System.out.println(userId.inverse().get("tom"));

        //Multimap系列接口集合 和Multiset很相似 可以理解成Multiset的map版本 对应的实现将上面的对应map改为Multimap
        //Multimap主要是为了一个键映射到多个值。换句话说，Multimap是把键映射到任意多个值的一般方式。
        //可以和传统的缓存Map<K, Collection<V>>进行对比
        Multimap<Integer, String> multimap = HashMultimap.create();
        //转换成Map<K, Collection<V>>格式 且对转换后的map做操作会影响原有的Multimap
        multimap.asMap();
        //添加键到单个值的映射
        multimap.put(1, "a");
        //添加多个值的映射
        multimap.putAll(2, Lists.newArrayList("a", "b", "c", "d"));
        //移除键到值的映射；如果有这样的键值并成功移除，返回true。
        multimap.remove(2, "b");
        multimap.put(2, "another");

        //移除一个key所有的映射值
        /// multimap.removeAll(1);
        //替换原有的映射值集合
        multimap.replaceValues(1, Lists.newArrayList("a", "b", "c", "d"));

        System.out.println(multimap);


        //Multiset
        Multiset<String> countMap = HashMultiset.create();
        //插入字符串"aa"2次
        countMap.add("aa", 3);
        //删除n次
        countMap.remove("aa", 1);
        //获取不重复的元素只获取到一个aa
        countMap.elementSet();
        //统计aa插入的次数
        countMap.count("aa");
        //和Map的entrySet类似 返回set类型的entry集 支持getElement()和getCount()方法
        countMap.entrySet();
        //给定元素固定次数
        countMap.setCount("aa", 5);

        countMap.add("bb", 1);
        countMap.add("bb", 2);

        //返回集合元素的总个数(重复的也记录 若想去重使用countMap.elementSet().size())
        countMap.size();

        System.out.println(countMap);

        System.out.println(JsonKit.toJson(countMap));

        for (Multiset.Entry entry : countMap.entrySet()) {
            System.out.println(entry.getElement() + " " + entry.getCount());
        }

        Table<Integer, Integer, Integer> weightedGraph = HashBasedTable.create();
        weightedGraph.put(1, 2, 4);
        weightedGraph.put(1, 3, 20);
        weightedGraph.put(2, 3, 5);
        //返回某一行的列对应值的map

        System.out.println(weightedGraph.row(1));
        //返回某一列的行对应值的map
        System.out.println(weightedGraph.column(3));
    }

    void fileOp() {
        File file=new File("/Users/ygzheng/Downloads/tmp/first/config/cmd/simple.html");

        //获取classpath根下的abc.xml文件url
        URL url = Resources.getResource("cmd/simple.html");
        System.out.println(url);

        try {
            //逐行读取 以utf-8编码
            ImmutableList<String> lines = Files.asCharSource(file, Charsets.UTF_8).readLines();
            System.out.println(Joiner.on("\n").join(lines));

            //读取单词
            Multiset<String> wordOccurrences = HashMultiset.create(
                //以空格拆分
                Splitter.on(CharMatcher.anyOf("</> "))
                    .trimResults()
                    .omitEmptyStrings()
                    .split(Files.asCharSource(file, Charsets.UTF_8).read()));

            System.out.println(wordOccurrences);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void transform() {
        List<String> listStr = Lists.newArrayList("1", "2", "3");
        //将字符串集合转换为Integer集合
        List<Integer> listInteger = listStr.stream().map(s -> Integer.valueOf(s)).collect(Collectors.toList());
        System.out.println(listInteger);
    }
}
