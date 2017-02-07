package jp.co.iccom.yamada_tetsuya.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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
	}
}