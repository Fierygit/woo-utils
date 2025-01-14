package com.pugwoo.wooutils.json;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestJson {
	
	public static class MyClass {
		private Map<String, Object> map;
		public Map<String, Object> getMap() {
			return map;
		}
		public void setMap(Map<String, Object> map) {
			this.map = map;
		}
	}

	public static class Student {
		@JsonSetter(nulls= Nulls.AS_EMPTY) // 这个是生效的，可以使得当为null时，设置为空字符串
		private String name = "default"; // 这里设置的默认值，会被json里的null值覆盖；但是如果json里面没有这个属性，那就是默认值了；所以这里还做不到，如果json里是null，怎么让它用默认值，而非转成null；除非改写setter方法，但也麻烦

		@JsonSetter(nulls= Nulls.AS_EMPTY) // 这个有效，当是null时，值为0
		private Integer age = 1;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
	}

	@Test
	public void testNullValue() {
		String json = "{\"name\":null,\"age\":null}";
		Student student = JSON.parse(json, Student.class);
		System.out.println(student.getName() + "," + student.getAge());
		assert student.getName().isEmpty();
		assert student.getAge() == 0;
	}
	
	@Test
	public void toJsonTest() {
		System.out.println("\n================ 转json示例");
		
		Date date = new Date();
		System.out.println("  date: " + date);
		System.out.println("toJson: " + JSON.toJson(new Date()));
		
		Map<String, Object> map = new HashMap<>();
		map.put("name", "nick");
		map.put("age", null);
		map.put(null, null);
		System.out.println(JSON.toJson(map));
		System.out.println(JSON.toJson(JSON.parse(JSON.toJson(map))));
	}
	
	@Test
	public void parseTest() {
		System.out.println("\n================ 解析json示例");
		
		System.out.println(JSON.parse("\"2017-03-03 15:34\"", Date.class));
		System.out.println(JSON.parse("\"2017年3月30日\"", Date.class));
		System.out.println(JSON.parse("\"  \"", Date.class));
		
		System.out.println();
		
		MyClass myclass = JSON.parse("{\"map\":\"\"}", MyClass.class);
		System.out.println(JSON.toJson(myclass));
	}
	
	@Test
	public void parseGenericTest() {
		System.out.println("\n================ 解析json示例-泛型1");
		String json1 = "[\"20180102\", \"20180306\"]";
		@SuppressWarnings("unchecked")
		List<Date> list = JSON.parse(json1, List.class, Date.class);
		System.out.println(list);
		
		System.out.println("\n================ 解析json示例-泛型2");
		String json2 = "{\"arr\":\"20180102\"}";
		@SuppressWarnings("unchecked")
		Map<String, Date> map = JSON.parse(json2, Map.class, String.class, Date.class);
		System.out.println(map);
	}
	
	@Test
	public void parseReferenceTest() {
		System.out.println("\n================ 解析json示例-类型引用1");
		String json1 = "[\"20180102\", \"20180306\"]";
		List<Date> list = JSON.parse(json1, new TypeReference<List<Date>>() {});
		System.out.println(list);
		
		System.out.println("\n================ 解析json示例-类型引用2");
		String json2 = "{\"arr\":\"20180102\"}";
		Map<String, Date> map = JSON.parse(json2, new TypeReference<Map<String, Date>>() {});
		System.out.println(map);
	}
	
	@Test
	public void cloneTest() {
		System.out.println("\n================ clone实例-不支持泛型");
		Map<String, Date> map = new HashMap<>();
		map.put("date", new Date());
		// clone 不支持泛型
		Map<String, Date> mapClone = JSON.clone(map);
		
		System.out.println("     map: " + map);
		System.out.println("mapClone: " + mapClone);
	}
	
	@Test
	public void cloneReferenceTest() {
		System.out.println("\n================ clone实例-类型引用");
		Map<String, Date> map = new HashMap<>();
		map.put("date", new Date());

		Map<String, Date> mapClone = JSON.clone(map, new TypeReference<Map<String, Date>>() {});
		
		System.out.println("     map: " + map);
		System.out.println("mapClone: " + mapClone);

		assert mapClone.get("date").getClass() == Date.class;

		mapClone = JSON.clone(map, String.class, Date.class);

		System.out.println("     map: " + map);
		System.out.println("mapClone: " + mapClone);

		assert mapClone.get("date").getClass() == Date.class;
	}
	
	@Test
	public void cloneReference2Test() {
		Map<String, Date> map = new HashMap<>();
		map.put("date", new Date());
		
		Map<String, Map<String, Date>> listItem = new HashMap<>();
		listItem.put("mapmap", map);
		
		List<Map<String, Map<String, Date>>> list = new ArrayList<>();
		list.add(listItem);
		
		TypeReference<List<Map<String, Map<String, Date>>>> typeReference =
				new TypeReference<List<Map<String, Map<String, Date>>>>() {};
		String listJson = JSON.toJson(list);
		
		List<Map<String, Map<String, Date>>> listClone = JSON.clone(list, typeReference);
		
		System.out.println("list source: " + list);
		System.out.println("list json  : " + listJson);
		System.out.println("list clone : " + listClone);

		assert listClone.get(0).get("mapmap").get("date").getClass() == Date.class;

		// 说明：这种方式不支持嵌套泛型，所以解析不了上面的类型
		listClone = JSON.clone(list, Map.class);
		System.out.println("list source: " + list);
		System.out.println("list json  : " + listJson);
		System.out.println("list clone : " + listClone);

		// assert listClone.get(0).get("mapmap").get("date").getClass() == Date.class; // 报错
	}

}
