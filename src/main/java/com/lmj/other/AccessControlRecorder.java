package com.lmj.other;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 访问控制记录器
 * 
 * 缺陷：没有自动清理功能，会一直存在于内存中；可以定时清理（或者其他条件满足时清理，如当队列尺寸过大时），最新访问时间远大于当前时间的队列；
 * 使用示例：
 * 
	static AccessControlRecorder accessControlRecorder= new AccessControlRecorder();
	static {
		guoAccessControlRecorder.setAccessTimesLimit(accessTimesLimit);
		guoAccessControlRecorder.setStatisticalPeriodInMillis(visitMinCycleInMillis);
	}
	
	isFrequentAccess = accessControlRecorder.isFrequentlyAccessedAndUpdateRecords(token);
 * @author 李梦杰
 * @date 2020-10-29
 */
public class AccessControlRecorder {
	private Long statisticalPeriodInMillis; // 统计周期
	private int timesLimit; // 访问次数限制
	Map<String, Queue<Long>> accessRecords = new HashMap<String, Queue<Long>>(); // 用户访问记录<用户标识，访问时间队列>
	
	/**
	 * 是否频繁访问
	 * @param userId
	 * @return
	 */
	public boolean isFrequentlyAccessedAndUpdateRecords(String userId) {
		boolean isFrequentAccess = false; // 是否频繁访问
		if(timesLimit < 1) { // 限制次数为-1或0，则不限制访问次数
			return isFrequentAccess;
		}
		Queue<Long> accessTimesQueue = getAccessTimesQueue(userId);
		// 判断累积访问次数是否超过限制
		int curRecordAccessTimes = accessTimesQueue.size();
		Long curAccessTimeInMills = Calendar.getInstance().getTimeInMillis();
		if(curRecordAccessTimes >= timesLimit) { // 当已记录访问次数大于等于周期内限制访问次数时
			Long earliestAccessTimeInMillsLong = accessTimesQueue.peek();
			
			if(earliestAccessTimeInMillsLong != null && (curAccessTimeInMills - earliestAccessTimeInMillsLong) <= statisticalPeriodInMillis) {
				isFrequentAccess = true; // 频繁访问，这次访问不做记录
			}else {
				accessTimesQueue.poll(); // 移除首元素
				accessTimesQueue.offer(curAccessTimeInMills); // 非频繁访问
			}
		}else {
			accessTimesQueue.offer(curAccessTimeInMills); // 非频繁访问
		}
		return isFrequentAccess;
	}
	
	private Queue<Long> getAccessTimesQueue(String userId) {
		Queue<Long> accessTimesQueue = accessRecords.get(userId);
		if(accessTimesQueue == null) {
			// 未找到，添加并返回
			accessTimesQueue = new LinkedList<Long>();
			accessRecords.put(userId, accessTimesQueue);
		}
		return accessTimesQueue;
	}
	
	
	public Long getStatisticalPeriodInMillis() {
		return statisticalPeriodInMillis;
	}

	public void setStatisticalPeriodInMillis(Long statisticalPeriodInMillis) {
		this.statisticalPeriodInMillis = statisticalPeriodInMillis;
	}

	public int getAccessTimesLimit() {
		return timesLimit;
	}

	public void setAccessTimesLimit(int accessTimesLimit) {
		this.timesLimit = accessTimesLimit;
	}

}
