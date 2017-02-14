package jp.co.iccom.kawashima_jun.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class test {
	public static void main(String[] args) {
		HashMap<String,String> branch = new HashMap<String,String>();
		HashMap<String,Long> branchsale = new HashMap<String,Long>();
		HashMap<String,String> commodity = new HashMap<String,String>();
		HashMap<String,Long> commoditysale = new HashMap<String,Long>();
		try {//支店定義ファイル
			File file = new File(args[0],"branch.lst");//ファイル読み込み
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			
			while((s = br.readLine()) != null) {//フォーマット判定
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches("[0-9]{3}")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branch.put(items[0], items[1]);//Map置く
				branchsale.put(items[0], 0L);
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
			br.close();
		} catch(FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		} catch(IOException e) {
			 System.out.println("予期せぬエラーが発生しました");
			 return;
		}
		try {//商品定義ファイル
			File file = new File(args[0],"commodity.lst");//ファイル読み込み
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s;
			
			while((s = br.readLine()) != null) {//フォーマット判定
				String[] items = s.split(",");
				if(items.length != 2 || !items[0].matches("[a-zA-Z0-9]{8}$")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodity.put(items[0], items[1]);//Map置く
				commoditysale.put(items[0], 0L);
				//System.out.println(items[0]);
				//System.out.println(items[1]);
			}
			br.close();
		} catch(FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
			
		} catch(IOException e) {
			 System.out.println("予期せぬエラーが発生しました");
			 return;
		}
		//売上ファイル連番判定
		HashMap<String,String> proseed = new HashMap<String,String>();
		ArrayList<String> filelist = new ArrayList<String>();
		try {
			File dir = new File(args[0]);//ディレクトリ
			File[] files  = dir.listFiles();
			
			for(int i = 0;i + 1< files.length; i++) {
				File file = files[i];  //ここまでだとファイルが格納されているルートがでる
				String filename = file.getName();  //ここでFileの要素をString化する(ファイル以前のルートが消える)
				if(filename.matches("([0-9]{8}).rcd$")){//数字8桁且つrcdファイルの判定(それ以外は候補から外れる)
					filelist.add(filename);//filelistに加える
				}
			}
			for(int i = 0;i + 1 < filelist.size(); i++) {//filelistの要素を存在する分だけ羅列
				String str = filelist.get(i).substring(0,8);//数字8桁
				String stl = filelist.get(i + 1).substring(0,8);
				int a = Integer.parseInt(str);//参照a
				int b = Integer.parseInt(stl);//参照b
				if( b - a != 1) {//もし、b-aが1でない＝ファイル間の空き
					 System.out.println("売上ファイル名が連番になっていません");
					break;
				}
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//売上ファイルの中身の判定
		HashMap<String,String> earnings = new HashMap<String,String>();
		try {
			File dir = new File(args[0]);
			File[] files  = dir.listFiles();
			for(int i = 0;i < filelist.size(); i++) {//売上ファイルのリストを存在する分羅列
				ArrayList<String> salefile = new ArrayList<String>();
				File file = new File(args[0],filelist.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				while((str = br.readLine()) != null) {
					salefile.add(str);
				}
				if(salefile.size() != 3){//売上ファイル内容が3行ではない(フォーマットの判定)
					System.out.println(file.getName()+"のフォーマットが不正です");
				}
				String branchcode = salefile.get(0);//支店コード
				String commoditycode = salefile.get(1);//商品コード
				String price = salefile.get(2);//値段
				if(!branch.containsKey(branchcode)){//
					System.out.println(file.getName()+"の支店コードが不正です");
				}
				if(!commodity.containsKey(commoditycode)) {
					System.out.println(file.getName()+"の商品コードが不正です");
				}
				long money = Long.parseLong(price);//値段をmoneyに置き換え
				long branchsum = branchsale.get(branchcode);//支店コードをMap(branchsale)から参照して変数branchsumに置き換える
				long commoditysum = commoditysale.get(commoditycode);//商品コードをMap(commoditysale)から参照して変数commoditysumに置き換える
				branchsum += money;//支店を参照して金額を足す
				commoditysum += money;//商品を参照して金額を足す
				branchsale.put(branchcode,branchsum);//Map(branchsale)に支店コード、支店金額を置く
				commoditysale.put(commoditycode, commoditysum);//Map(commoditysale)に商品コード、商品金額を置く
				if(branchsum > 9999999999L){
					System.out.println("合計金額が10桁を超えました");
					return;
				}
			}
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			
			return;
		} catch(IndexOutOfBoundsException e) {//配列の範囲外
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//支店別に書き込み(降順)
		BufferedWriter bw = null;
		try {
			File writefile = new File(args[0],"branch.out");
			FileWriter fw = new FileWriter(writefile);
			bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(branchsale.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
				
				public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s: entries){
				bw.write(s.getKey() + "," + branch.get(s.getKey()) + "," +s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally{
			if(bw !=null){
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
		}//商品別に書き込み(降順)
		bw = null;
		try {
			File writefile = new File(args[0], "commodity.out");
			FileWriter fw = new FileWriter(writefile);
			bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(commoditysale.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

				public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s: entries){
				bw.write(s.getKey() + "," + commodity.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			if(bw !=null){
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}
		}
		if(!outPutFiles = args[0],"branch.out",branch,branchsale);
			
		if(!outPutFiles = args[0],"commodity.out",commodity,commoditysale);
		
		public static boolean outPutfile(String dir,String fileName,HashMap<String,String>,HashMap<String,Long>);
			try {//支店定義ファイル
				File file = new File(dir,fileName);//ファイル読み込み
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String s;
				
				while((s = br.readLine()) != null) {//フォーマット判定
					String[] items = s.split(",");
					if(items.length != 2 || !items[0].matches("[0-9]{3}")) {
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
				File writefile = new File(args[0], "commodity.out");
				FileWriter fw = new FileWriter(writefile);
				bw = new BufferedWriter(fw);
				ArrayList<Map.Entry<String,Long>> entries = new ArrayList<Map.Entry<String,Long>>(commoditysale.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

					public int compare(Entry<String,Long> entry1,Entry<String,Long> entry2) {
					return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
					}
				});
				for(Entry<String, Long> s: entries){
					bw.write(s.getKey() + "," + commodity.get(s.getKey()) + "," + s.getValue());
					bw.newLine();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				if(bw !=null){
					try {
						bw.close();
						return false;
					} catch (IOException e) {
						System.out.println("予期せぬエラーが発生しました");
						return;
						return false;
					}
				}
			}
	}
}