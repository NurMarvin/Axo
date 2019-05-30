package de.nurmarvin.axo.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class UtilMath {
	public static double trim(int degree, double d) {
		StringBuilder format = new StringBuilder("#.#");
		
		for (int i=1 ; i<degree ; i++)
			format.append("#");

		DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.GERMANY);
		DecimalFormat twoDForm = new DecimalFormat(format.toString(), symb);
		return Double.valueOf(twoDForm.format(d));
	}
}