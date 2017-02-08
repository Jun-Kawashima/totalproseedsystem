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
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
			br.close();
		} catch(IOException e) {
			System.out.println("支店定義ファイルが存在しません");
		}


		HashMap<String,String> commodity = new HashMap<String,String>();
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
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
			br.close();
		} catch(IOException e) {
			System.out.println("商品定義ファイルが存在しません");
		}


		HashMap<String,String> proseed = new HashMap<String,String>();
		try {

			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			LinkedList<String> filelist = new LinkedList<String>();

			for(int i = 0;i < files.length; i++) {
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
			for(int i = 0;i < filelist.size(); i++) {
				String str = filelist.get(i).substring(0,8);

				String stl = filelist.get(i + 1).substring(0,8);
				int a = Integer.parseInt(str);
				int b = Integer.parseInt(stl);
				if( b - a != 1) {
					 System.out.println("売上ファイルが連番になっていません");
					return;
				}
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
}