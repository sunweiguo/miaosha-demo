package com.swg.miaosha.key;

public class OrderKey extends BasePrefix{

	private OrderKey(String prefix) {
		super(prefix);
	}
	public static OrderKey getMiaoShaOrderByUidGid = new OrderKey( "moug");
}
