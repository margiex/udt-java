package udt;

import java.security.MessageDigest;

import udt.util.UDTStatistics;

public class TestUDTInputStream extends UDTTestBase{

	public void test1()throws Exception{
		UDTStatistics stat=new UDTStatistics("test");
		UDTInputStream is=new UDTInputStream(null, stat);
		byte[] data1="this is ".getBytes();
		byte[] data2="a test".getBytes();
		byte[] data3=" string".getBytes();
		String digest=computeMD5(data1,data2,data3);
		is.haveNewData(0, data1);
		is.haveNewData(1, data2);
		is.haveNewData(2, data3);
		is.noMoreData();
		is.setBlocking(false);
		readAll(is,8);
		assertEquals(digest,stat.getDigest());
	}
	
	public void testInOrder()throws Exception{
		UDTStatistics stat=new UDTStatistics("test");
		UDTInputStream is=new UDTInputStream(null, stat);
		is.setBlocking(false);
		byte[]data=getRandomData(10*1024);
		
		byte[][]blocks=makeChunks(10,data);
		String digest=computeMD5(blocks);
		
		for(int i=0;i<10;i++){
			is.haveNewData(i, blocks[i]);
		}
		is.noMoreData();
		
		readAll(is,512);
		assertEquals(digest,stat.getDigest());
	}
	
	public void testRandomOrder()throws Exception{
		UDTStatistics stat=new UDTStatistics("test");
		UDTInputStream is=new UDTInputStream(null, stat);
		is.setBlocking(false);
		byte[]data=getRandomData(100*1024);
		
		byte[][]blocks=makeChunks(10,data);
		String digest=computeMD5(blocks);
		
		byte[]order=new byte[]{9,7,5,3,1,2,0,4,6,8};
		
		for(int i : order){
			is.haveNewData(i, blocks[i]);
		}
		readAll(is,512,true);
		
		assertEquals(digest,stat.getDigest());
	}
	
	//read and discard data from the given input stream
	//returns the md5 digest of the data
	protected String readAll(UDTInputStream is, int bufsize,boolean sendNoMoreData)throws Exception{
		MessageDigest d=MessageDigest.getInstance("MD5");
		int c=0;
		byte[]buf=new byte[bufsize];
		while(true){
			c=is.read(buf);
			is.noMoreData();
			if(c==-1)break;
			else{
				d.update(buf,0,c);
			}
		}
		return UDTStatistics.hexString(d);
	}
	
}
