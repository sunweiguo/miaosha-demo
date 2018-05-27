package com.swg.miaosha.key;

public class MiaoshaKey extends BasePrefix{

	private MiaoshaKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
}
