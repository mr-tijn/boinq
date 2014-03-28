/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.client.util;




public class ColorTools {

	public ColorTools() {
		super();
		// TODO Auto-generated constructor stub
	}

	static public String getColor(Double score, Double minValue,Double maxValue, Double minHue, Double maxHue){
		if (score == null) return "grey";
 		Double posScore=(score-minValue)/(maxValue-minValue);
		Double hue=(posScore*(maxHue-minHue))+minHue;
		Double saturation = 0.9;
		Double brightness = 0.9;
		
		Double Chroma = brightness * saturation;
		Double Hdash = hue / 60.0;
		Double X = Chroma * (1.0 - Math.abs((Hdash % 2.0) - 1.0));
		Double red = 0.;
		Double green = 0.;
		Double blue = 0.;
		if(Hdash < 1.0)
		{
			red = Chroma;
			green = X;
		}
		else if(Hdash < 2.0)
		{
			red = X;
			green = Chroma;
		}
		else if(Hdash < 3.0)
		{
			green = Chroma;
			blue = X;
		}
		else if(Hdash < 4.0)
		{
			green= X;
			blue = Chroma;
		}
		else if(Hdash < 5.0)
		{
			red = X;
			blue = Chroma;
		}
		else if(Hdash < 6.0)
		{
			red = Chroma;
			blue= X;
		}
	 
		Double Min = brightness - Chroma;
	 
		red+= Min;
		green += Min;
		blue += Min;
		
		String colorString = "#" + Integer.toHexString(Math.round(255*red.floatValue())) +  Integer.toHexString(Math.round(255*green.floatValue())) + Integer.toHexString(Math.round(255*blue.floatValue()));
		return colorString;
	}
}
