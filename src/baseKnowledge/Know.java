package baseKnowledge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Know {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * short 基础类型相关
		 * float和double很少使用，只用于科学和工程计算，在计算过程中极容易丢失精度
		 * byte/short/char ->int->long->float->double
			byte/short/char之间不互相转换，它们在运算时都会转换为int

		 */
//		short s1=1;s1=s1+1; //编译出错， s1+1的类型是int
		short s2=1;s2+=1; //System.out.println(s2);
		byte b= 1; b=(byte) (b+2);
		
		/**
		 * 四舍五入方式
		 * 变量增加1/2之后求其float
		 */
//		System.out.println(Math.round(11.5));//12
//		System.out.println(Math.round(-11.5));//-11


		/**
		 * List Set继承Collection
		 * Map否
		 */
		List<String> a = null;
		Set<String> bb=null;
		Map<String, String> cc=null;
		
		/**
		 * 4个线程：两个每次加1，两个每次减1
		 * 内部类，没考虑顺问题
		 */
		testThread();
		
		
	}
	/**
	 * 线程相关
	 * @return 
	 */
	static void testThread(){
		Know kn = new Know();
		inc inc=kn.new inc();
		dec dec=kn.new dec();
		for(int i=0;i<2;i++){
			Thread td = new Thread(inc);
			td.start();
			td = new Thread(dec);
			td.start();
		}
	}
	private int j ;
	private synchronized void inc(){
		j++;
		System.out.println(Thread.currentThread().getName()+"-inc:"+j);
	}
	private synchronized void dec(){
		j--;
		System.out.println(Thread.currentThread().getName()+"-dec:"+j);
	}
	class inc implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<100;i++){
				inc();
			}
		}
	}
	class dec implements Runnable{
		@Override
		public void run() {
			for(int i=0;i<100;i++){
				dec();
			}
		}
	}
	
}


