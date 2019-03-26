package com.luckyli.extral;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 恶补TreeSet的headSet用法
 */
public class TresetTest {


    public static void main(String[] args) {
        SortedSet<String> sortedSet = new TreeSet<String>();//定义一个集合进行排序

        sortedSet.add("5");
        sortedSet.add("2");
        sortedSet.add("3");
        sortedSet.add("8");
        //输出集合元素，可以看到集合元素已经处于排序状态，输出【2，3，5，8】
        System.out.println(sortedSet);
        //输出集合里的第一个元素2
        System.out.println(sortedSet.first());
        //输出集合里最后一个元素
        System.out.println(sortedSet.last());
        //输出小于4的集合，不包含4，输出【2，3】
        System.out.println(sortedSet.headSet("4"));
        //输出大于5的集合，如果set集合中有5，子集中还应该有5，输出【5，8】
        System.out.println(sortedSet.tailSet("5"));
        //输出大于2，小于5的子集，包括2，不包括5，输出集合【2，3】
        System.out.println(sortedSet.subSet("2","5"));

        /**
         *  输出结果:
         *  [2, 3, 5, 8]
         *  2
         *  8
         *  [2, 3]
         *  [5, 8]
         *  [2, 3]
         */

        // Creating an empty TreeSet
        TreeSet<String> tree_set = new TreeSet<String>();

        // Adding the elements using add()
        tree_set.add("Welcome");
        tree_set.add("To");
        tree_set.add("Geek");
        tree_set.add("4");
        tree_set.add("Geeks");
        tree_set.add("TreeSet");

        // Creating the headSet tree
        TreeSet<String> head_set = new TreeSet<String>();

        // Limiting the values till 5
        head_set = (TreeSet<String>)tree_set.headSet("To");

        // Creating an Iterator
        Iterator iterate;
        iterate = head_set.iterator();

        // Displaying the tree set data
        System.out.println("The resultant values till head set: ");

        // Iterating through the headSet
        while (iterate.hasNext()) {
            System.out.println(iterate.next() + " ");
        }
        /**
         * The resultant values till head set:
         * 4
         * Geek
         * Geeks
         */

        
    }


}
