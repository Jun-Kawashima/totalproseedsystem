package jp.co.iccom.yamada_tetsuya.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BranchList {
	public static void main(String[] args) {
		HashMap<String,String> branch = new HashMap<String,String>();
		HashMap<String,Long> branchsale = new HashMap<String,Long>();
		HashMap<String,String> commodity = new HashMap<String,String>();
		HashMap<String,Long> commoditysale = new HashMap<String,Long>();
		try {
			File file = new File(args[0],"branch.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;

			while((s = br.readLine()) != null) {
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches("[0-9]{3}")) {
					System.out.println("支店定義ファイルフォーマットが不正です");
					return;
				}
				branch.put(items[0], items[1]);
				branchsale.put(items[0], 0L);
				System.out.println(items[0]);
				System.out.println(items[1]);
			}
			br.close();
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
		}

		try {
			File file = new File(args[0],"commodity.lst");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;

			while((s = br.readLine()) != null) {
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches("[a-zA-Z0-9]{8}$")) {
					System.out.println("商品定義ファイルフォーマットが不正です");
					return;
				}
				commodity.put(items[0], items[1]);
				commoditysale.put(items[0], 0L);
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
			br.close();
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
		}

		HashMap<String,String> proseed = new HashMap<String,String>();
		ArrayList<String> filelist = new ArrayList<String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();

			for(int i = 0;i + 1< files.length; i++) {
				File file = files[i];
				//System.out.println(file);
				//ここまでだとファイルが格納されているルートがでる
				String filename = file.getName();
				//System.out.println(filename);
				//ここでFileの要素をString化する(ファイル以前のルートが消える)
				if(filename.matches("([0-9]{8}).rcd$")){
					filelist.add(filename);
					//System.out.println(filename);
				}
			}
			for(int i = 0;i + 1 < filelist.size(); i++) {
				String str = filelist.get(i).substring(0,8);
				String stl = filelist.get(i + 1).substring(0,8);
				int a = Integer.parseInt(str);
				int b = Integer.parseInt(stl);
				if( b - a != 1) {
					 System.out.println("売上ファイルが連番になっていません");
					break;
				}
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}

		HashMap<String,String> earnings = new HashMap<String,String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			for(int i = 0;i < files.length; i++) {
				ArrayList<String> salefile = new ArrayList<String>();
				File file = new File(args[0],filelist.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				while((str = br.readLine()) != null) {
					salefile.add(str);
				}
				if(salefile.size() != 3){
					System.out.println(file.getName()+"のフォーマットが不正です");
				}
				String branchcode = salefile.get(0);
				String commoditycode = salefile.get(1);
				String price = salefile.get(2);
				if(!branch.containsKey(branchcode)){
					System.out.println(file.getName()+"の支店コードが不正です");
				}
				if(!commodity.containsKey(commoditycode)) {
					System.out.println(file.getName()+"の商品コードが不正です");
				}
				long money = Long.parseLong(price);
				long branchsum = branchsale.get(branchcode);
				long commoditysum = commoditysale.get(commoditycode);
				branchsum += money;
				commoditysum += money;
				branchsale.put(branchcode,money);
				commoditysale.put(commoditycode, money);
				if(branchsum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
				}
			}
		}catch(Exception e) {
		}
		ArrayList<String> salefile = new ArrayList<String>();
		HashMap<String,String> branchout = new HashMap<String,String>();
		try {
			File writefile = new File(args[0],"branch.out");
			FileWriter fw = new FileWriter(writefile);
			BufferedWriter bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(branchsale.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

				public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String,Long> s: entries){
				System.out.println("s.getKey():" + s.getKey());
				System.out.println("s.getValue() :" + s.getValue());
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}