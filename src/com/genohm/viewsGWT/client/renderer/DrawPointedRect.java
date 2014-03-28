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
package com.genohm.viewsGWT.client.renderer;

import com.smartgwt.client.widgets.drawing.DrawPath;
import com.smartgwt.client.widgets.drawing.Point;

public class DrawPointedRect extends DrawPath {
	
	
	protected int top;
	protected int left;
	protected int width;
	protected int height;
	protected Boolean direction = true;
	protected int tipwidth = 10;
	
	
	@Override
	public void draw() {
		if (width < tipwidth) return;
		if (direction) {
//			if (width <= tipwidth) {
				super.setPoints(new Point(left, top), new Point(left + width, top), new Point(left+width+tipwidth, top + height/2), new Point(left+width, top + height), new Point(left, top + height), new Point(left, top));
//			} else {
//				super.setPoints(new Point(left, top), new Point(left + width - tipwidth, top), new Point(left+width, top + height/2), new Point(left+width-tipwidth, top + height), new Point(left, top + height), new Point(left, top));
//			}
		} else {
//			if (width <= tipwidth) {
				super.setPoints(new Point(left, top), new Point(left + width, top), new Point(left+width, top + height), new Point(left, top + height), new Point(left - tipwidth, top + height/2), new Point(left, top));
//			} else {
//				super.setPoints(new Point(left + tipwidth, top), new Point(left + width, top), new Point(left+width, top + height), new Point(left + tipwidth, top + height), new Point(left, top + height/2), new Point(left + tipwidth, top));				
//			}
		}
		super.draw();
	}
	
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public Boolean getDirection() {
		return direction;
	}

	public void setDirection(Boolean direction) {
		this.direction = direction;
	}

	public int getTipwidth() {
		return tipwidth;
	}

	public void setTipwidth(int tipwidth) {
		this.tipwidth = tipwidth;
	}
	
	
}
