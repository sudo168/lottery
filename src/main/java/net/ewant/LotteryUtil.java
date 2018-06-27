package net.ewant;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotteryUtil {
	
	/**
     * 抽奖
     * @param prizes
     *            任何需要概率计算的对象（T：必须为拥有probability属性的对象或数值型对象）
     * @param field
     *            具体概率字段名（实体对象默认probability，数值型对象可不传）
     * @return
     *         prizes 列表的中奖index, -1 则表示抽奖失败
     */
    public static <T> int lottery(List<T> prizes,String... field) {
    	try {
    		if (prizes == null || prizes.isEmpty()) {
        		return -1;
        	}
    		int size = prizes.size();
    		String f = "probability";
    		T t = prizes.get(0);
			boolean isBasicType = false;
			if(t instanceof Double || t instanceof Float  || t instanceof String){
				isBasicType = true;
			}else{
				if(field != null && field.length > 0){
	        		f = field[0];
	        	}
			}
        	// 计算总概率，这样可以保证不一定总概率是1 (关系到权重的问题)
        	double sumRate = 0d;
			List<Double> orignalRates = new ArrayList<>(size);
			
			for (T gift : prizes) {
				double probability = 0;
				if (isBasicType) {
					probability = Double.parseDouble(gift.toString());
				}else{
					Method method = gift.getClass().getMethod("get"+f.substring(0, 1).toUpperCase()+f.substring(1));
					Object invoke = method.invoke(gift);
					if (invoke != null) {
						probability = Double.parseDouble(invoke.toString());
					}
				}
			    if (probability < 0) {
			        probability = 0;
			    }
			    sumRate += probability;
			    orignalRates.add(probability);
			}
			
			// 计算每个物品在总概率的基础下的概率情况（构建区间，核心部分）
	    	List<Double> sortOrignalRates = new ArrayList<>(size);
	    	Double tempSumRate = 0d;
	    	for (double rate : orignalRates) {
	    		tempSumRate += rate;
	    		sortOrignalRates.add(tempSumRate / sumRate);
	    	}
	    	// 根据区间值来获取抽取到的物品索引，理论上random随机概率是均等的，因此区间的大小决定了概率大小
	    	double nextDouble = Math.random();
	    	sortOrignalRates.add(nextDouble);
	    	Collections.sort(sortOrignalRates);
	    	
	    	return sortOrignalRates.indexOf(nextDouble);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return -1;
    }

    static class Gift {
    	private String name;
    	private double probability;
    	private int total;
    	private int count;


		public Gift(String name, double probability) {
			this.name = name;
			this.probability = probability;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public double getProbability() {
			return probability;
		}

		public void setProbability(double probability) {
			this.probability = probability;
		}
	}

	public static void main(String[] args) {
		List<Gift> prizes = new ArrayList<>();
		prizes.add(new Gift("50金币", 0.06));
		prizes.add(new Gift("充气娃娃1个", 0.01));
		prizes.add(new Gift("10元话费", 0.03));
		prizes.add(new Gift("10金币", 0.1));
		prizes.add(new Gift("谢谢惠顾", 0.8));

		int p0 = 0, p1 = 0, p2 = 0, p3 = 0, p4 = 0;
		long start = System.currentTimeMillis();
		int total = 100000;
		for (int i = 0; i < total; i++) {
			int lottery = lottery(prizes);
			if(lottery == 0){
				p0++;
			}else if(lottery == 1){
				p1++;
			}else if(lottery == 2){
				p2++;
			}else if(lottery == 3){
				p3++;
			}else if(lottery == 4){
				p4++;
			}
		}

		System.out.println(p0 + " 人抽到: " + prizes.get(0).getName());
		System.out.println(p1 + " 人抽到: " + prizes.get(1).getName());
		System.out.println(p2 + " 人抽到: " + prizes.get(2).getName());
		System.out.println(p3 + " 人抽到: " + prizes.get(3).getName());
		System.out.println(p4 + " 人抽到: " + prizes.get(4).getName());

		start = System.currentTimeMillis() - start;
		System.out.println("参与人数: " + total + ", 耗时: " + start + "ms");
	}
}
