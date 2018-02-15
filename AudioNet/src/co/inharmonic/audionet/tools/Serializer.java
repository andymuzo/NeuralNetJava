package co.inharmonic.audionet.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import co.inharmonic.audionet.neuralnet.NeuralNet;
 
public class Serializer {
 
   public void serializeNet(NeuralNet net, String filename){
	   
	   try{
 
		FileOutputStream fout = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(net);
		oos.close();
		System.out.println("Neural Net saved as " + filename);
 
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
   }
   
   public NeuralNet deserialzeNet(String filename){
	   
	   NeuralNet net;
 
	   try{
 
		   FileInputStream fin = new FileInputStream(filename);
		   ObjectInputStream ois = new ObjectInputStream(fin);
		   net = (NeuralNet) ois.readObject();
		   ois.close();
 
		   System.out.println("loaded ANN from " + filename);
		   
		   return net;
 
	   }catch(Exception ex){
		   ex.printStackTrace();
		   return null;
	   } 
   }
}