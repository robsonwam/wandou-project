package cn.edu.thu.log.clean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.thu.log.preprocessrule.NoiseIdentifyRule;
import cn.edu.thu.log.read.LogBuffer;

public class NoiseIdentify {

	private Set<String> nrule;
	private ArrayList<String> logTagList;
	private ArrayList<Object> logContentList;
	private int flag = 1;

	public NoiseIdentify() {
		// logCleanRules=new HashSet<Map<String,String>>();
		nrule = new HashSet<String>();
		logTagList = new ArrayList<String>();
		logContentList = new ArrayList<Object>();
	}

	// 判断一个或多个字段中是否出现指定的noise规则
	public boolean noiseStrIdentify(LogBuffer record,
			NoiseIdentifyRule noiserule) {
		nrule.addAll(noiserule.getAllNoiseIdentifyRules());
		logTagList.addAll(record.getLogTagList());
		logContentList.addAll(record.getLogContent());

		Iterator contentit = logContentList.iterator();
		while (contentit.hasNext() && flag == 1) {
			// content是要分析的字段对应的内容
			String content = contentit.next().toString();
			Iterator<String> ruleit = nrule.iterator();
			// 对每个字段分别识别是否存在指定rule
			while (ruleit.hasNext()) {
				String noisestr = ruleit.next();
				Pattern pattern = Pattern.compile(noisestr,
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(content);
				// 如果匹配，则这个字段后面的noise规则不用检查，跳出这条noise规则的检查
				if (matcher.find()) {
					flag = 0;// 设置以后，外部循环也不满足条件，其他字段不用执行检查，直接结束执行
					break;
				}
			}
			// 如果匹配，则flag保持为1
		}
		if (flag == 0)
			return false;
		
		return true;
	}

	// 判断是否出现case相同，且activity之间小于最小时间间隔，如果出现则删除这个case的所有记录
	// 可以先记录这个case，当载入后，检查caseID，如果存在这个记录，则直接删除这个caseID对应的记录
	public boolean minIdentify() {
		return false;

	}

	// 判断是否出现case相同，总访问时间超过规定时间长度，如果出现则删除这个case的所有记录
	// 可以先记录这个case，当载入后，检查caseID，如果存在这个记录，则直接删除这个caseID对应的记录
	public boolean maxIdentify() {
		return false;

	}

	// 综合判断，三条都符合则保留这条记录，返回true
	public boolean noiseIdentify(LogBuffer record, NoiseIdentifyRule noiserule) {
		if (noiseStrIdentify(record, noiserule) && minIdentify()
				&& maxIdentify())
			return true;
		return false;

	}
}
