package jp.co.iccom.kawashima_jun.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Calcsale {
	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		HashMap<String, String> branchName = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		HashMap<String, String> commodityName = new HashMap<String, String>();
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();

		if(!readFile(args[0], "branch.lst", "[0-9]{3}", "支店", branchName, branchSales))
			return;
		if(!readFile(args[0], "commodity.lst", "[a-zA-Z0-9]{8}$", "商品", commodityName, commoditySales))
			return;

		//売上ファイル連番判定
		HashMap<String, String> proceed = new HashMap<String, String>();
		ArrayList<String> fileList = new ArrayList<String>();
		try {
			File dir = new File(args[0]);//ディレクトリ
			File[] files  = dir.listFiles();
			for(int i = 0; i < files.length; i++) {
				//ここまでだとファイルが格納されているルートがでる
				File file = files[i];
				//ここでFileの要素をString化する(ファイル以前のルートが消える)
				String fileName = file.getName();
				//数字8桁且つrcdファイルの判定(それ以外は候補から外れる)
				if(fileName.matches("([0-9]{8}).rcd$") && file.isFile()){
					//filelistに加える
					fileList.add(fileName);
				}
			}
			for(int i = 0; i < fileList.size() - 1; i++) {
				String str = fileList.get(i).substring(0, 8);
				String stl = fileList.get(i + 1).substring(0, 8);
				int now = Integer.parseInt(str);
				int next = Integer.parseInt(stl);
				//もし、next - now が1でない＝ファイル間の空き
				if( next - now != 1) {
					 System.out.println("売上ファイル名が連番になっていません");
					 return;
				}
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//売上ファイルの中身の判定
		HashMap<String, String> earnings = new HashMap<String, String>();
		try {
			for(int i = 0; i < fileList.size(); i++) {
				ArrayList<String> saleFile = new ArrayList<String>();
				File file = new File(args[0], fileList.get(i));
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str;
				while((str = br.readLine()) != null) {
					saleFile.add(str);
				}
				br.close();
				//売上ファイル内容が3行ではない(フォーマットの判定)
				if(saleFile.size() != 3){
					System.out.println(file.getName()+"のフォーマットが不正です");
					return;
				}
				//支店コード
				String branchCode = saleFile.get(0);
				if(!branchName.containsKey(branchCode)){
					System.out.println(file.getName() + "の支店コードが不正です");
					return;
				}
				//商品コード
				String commodityCode = saleFile.get(1);
				if(!commodityName.containsKey(commodityCode)) {
					System.out.println(file.getName() + "の商品コードが不正です");
					return;
				}
				String price = saleFile.get(2);
				long money = Long.parseLong(price);
				long branchSum = branchSales.get(branchCode);
				long commoditySum = commoditySales.get(commodityCode);
				branchSum += money;
				if(branchSum > 9999999999L) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				commoditySum += money;
				if(commoditySum > 9999999999L) {
					System.out.println("合計金額が10桁を超えました");
					return;
				}
				branchSales.put(branchCode, branchSum);
				commoditySales.put(commodityCode, commoditySum);
			}
		} catch(NumberFormatException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} catch(IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		if(!output(args[0], "branch.out", branchSales, branchName))
			return;
		if(!output(args[0], "commodity.out", commoditySales, commodityName))
			return;
	}
	public static boolean readFile(String path, String fileName, String format, String name, HashMap<String, String> branchMap, HashMap<String, Long> branchSaleMap){
		BufferedReader br = null;
		try {
			//ファイル読み込み
			File file = new File(path, fileName);
			if(!file.exists()){
				System.out.println(name + "定義ファイルが存在しません");
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			String str;
			while((str = br.readLine()) != null) {
				String[] items = str.split(",");
				if(items.length != 2 || !items[0].matches(format)) {
					System.out.println(name + "定義ファイルのフォーマットが不正です");
					return false;
				}
				//Map置く
				branchMap.put(items[0], items[1]);
				branchSaleMap.put(items[0], 0L);
			}
		} catch(IOException e) {
			 System.out.println("予期せぬエラーが発生しました1");
			return false;
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		}
		return true;
	}
	public static boolean output(String path, String fileName, HashMap<String, Long> branchMap ,HashMap<String, String> branchSaleMap){
		BufferedWriter bw = null;
		try {
			File writeFile = new File(path, fileName);
			FileWriter fw = new FileWriter(writeFile);
			bw = new BufferedWriter(fw);
			ArrayList<Map.Entry<String, Long>> entries = new ArrayList<Map.Entry<String, Long>> (branchMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {

				public int compare(Entry<String, Long> entry1,Entry<String, Long> entry2) {
				return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
				}
			});
			for(Entry<String, Long> s: entries){
				bw.write(s.getKey() + "," + branchSaleMap.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally{
			if(bw !=null){
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return false;
				}
			}
		}
		return true;
	}
}