package com.adayo.app.btphone.utils;

import android.util.Log;

import com.adayo.app.btphone.bean.LinkManDataBean;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	private static final String TAG = ListUtils.class.getSimpleName();

	/**
	 * 设计思路：获取第一个拼音字母（TYPE_CHARACTER）加入List集合中当前字母开头的联系人（TYPE_DATA）之前显示，在
	 * adapter中判断是TYPE_CHARACTER类型就只显示首字母，是TYPE_DATA类型就正常显示数据
	 * @param list
	 */
	public static  void sortList(List<LinkManDataBean> list){
		Log.i(TAG,"list.size() : "+list.size());
		List<LinkManDataBean> list1 = new ArrayList<>();
		LinkManDataBean dataBean = new LinkManDataBean(list.get(0).getPeopleInfo(),LinkManDataBean.TYPE_CHARACTER);
		list1.add(dataBean);
		dataBean = new LinkManDataBean(list.get(0).getPeopleInfo(),LinkManDataBean.TYPE_DATA);
		list1.add(dataBean);
		String currentCharacter = getFirstCharacter(list.get(0).getItemEn());
		for(int i=1;i<list.size();i++){
			if(getFirstCharacter(list.get(i).getItemEn()).compareTo(currentCharacter)!=0){
				currentCharacter = getFirstCharacter(list.get(i).getItemEn());
				dataBean = new LinkManDataBean(list.get(i).getPeopleInfo(),LinkManDataBean.TYPE_CHARACTER);
				dataBean.setItemEn(currentCharacter);
				list1.add(dataBean);
				dataBean = new LinkManDataBean(list.get(i).getPeopleInfo(),LinkManDataBean.TYPE_DATA);
				list1.add(dataBean);
			}else{
				list1.add(list.get(i));
			}
		}
		list.clear();
		for(LinkManDataBean bean:list1){
			list.add(bean);
		}
	}
	
	public static String getFirstCharacter(String str){
		return str.substring(0, 1);
	}

}
