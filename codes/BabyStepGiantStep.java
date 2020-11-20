/* Implementing BabyStepGiantStep algo
** Author: Richa */

import java.math.BigInteger;
import java.util.HashMap;

public class BabyStepGiantStep {

	public static BigInteger x_upper_limit = (new BigInteger("2")).pow(24); // I have chosen x<2^24 here. Will also work for higher powers (but will take more time).

	public static void main(String args[]) {
		BigInteger g = new BigInteger("2");
		BigInteger p = new BigInteger("21766174458617435773191008891802753781907668374255538511144643224689886235383840957210909013086056401571399717235807266581649606472148410291413364152197364477180887395655483738115072677402235101762521901569820740293149529620419333266262073471054548368736039519702486226506248861060256971802984953561121442680157668000761429988222457090413873973970171927093992114751765168063614761119615476233422096442783117971236371647333871414335895773474667308967050807005509320424799678417036867928316761272274230314067548291133582479583061439577559347101961771406173684378522703483495337037655006751328447510550299250924469288819");
        
        BigInteger expected_x = new BigInteger("236609");  // random value 
		BigInteger h = g.modPow(expected_x, p);
	
		int calculated_x = solveBabyStepGiantStep(h,g,p);

	    if (calculated_x < 0) {
	    	System.out.println("\nCould not find x!");
	    } else{
			System.out.println("\nFound X..!! \n\n x = " + calculated_x);
	     	if(calculated_x == expected_x.intValue()) {
	    		System.out.println("\nThe value matches with expected x..!");
	    	}	    	
	    }
	}


	public static int solveBabyStepGiantStep(BigInteger h, BigInteger g, BigInteger p) {  
	  
	    BigInteger m = sqrt(x_upper_limit); 
	    BigInteger gPow_m = g.modPow(m,p);
	    HashMap<String, Integer> Arr1 = new HashMap<String, Integer>();
	  	int i,j;

	  	for(j=0; j<m.intValue(); j++) {
	    	BigInteger lhs = h.multiply(g.pow(j)).mod(p);	  		
	  		Arr1.put(lhs.toString(), j);
	  	}

	  	j=-1;
	  	for(i=0; i<m.intValue(); i++) {
	  		BigInteger rhs = gPow_m.pow(i).mod(p);
	  		if(Arr1.containsKey(rhs.toString())) {
	  			j = Arr1.get(rhs.toString());
	  			break;
	  		}
	  	} 

	    if(j < 0) {
	    	return -1;
	    } else {
	    	return ((i * m.intValue()) - j);	    	
	    }
	}  


	// Following sqrt function has been taken from web sources:
	public static BigInteger sqrt(BigInteger x) {
	    BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
	    BigInteger div2 = div;
	    // Loop until we hit the same value twice in a row, or wind
	    // up alternating.
	    for(;;) {
	        BigInteger y = div.add(x.divide(div)).shiftRight(1);
	        if (y.equals(div) || y.equals(div2))
	            return y;
	        div2 = div;
	        div = y;
	    }
	}
	
}
