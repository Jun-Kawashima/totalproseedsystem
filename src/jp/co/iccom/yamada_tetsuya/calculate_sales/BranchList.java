package jp.co.iccom.yamada_tetsuya.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

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
				//System.out.println(items[0]);
				//System.out.println(items[1]);
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
		LinkedList<String> filelist = new LinkedList<String>();
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

		LinkedList<String> salefile = new LinkedList<String>();
		HashMap<String,String> earnings = new HashMap<String,String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			for(int i = 0;i < files.length; i++) {
				File file = new File(args[0],filelist.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while((s = br.readLine()) != null) {
					String str = s;
					filelist.add(str);
				}
				System.out.println(filelist);
				String branchcode = proseed.get(0);
				String commoditycode = proseed.get(1);
				String price = proseed.get(2);
				long money = Long.parseLong(price);
				long sum = branchsale.get(1);

				branchsale.put(branchcode,money);
					System.out.println(sum);
				commoditysale.put(branchcode, money);
				if(sum > 999999999L){
					//System.out.println("合計金額が10桁を超えました");
				}
			}
		}catch(Exception e) {

		}
	}
}