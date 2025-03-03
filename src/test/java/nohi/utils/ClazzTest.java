package nohi.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * <h3>nohi-doc</h3>
 *
 * @author NOHI
 * @description <p></p>
 * @date 2025/03/03 12:44
 **/
@Slf4j
class ClazzTest {
    @Data
    class DataVo {
        int id;
        String name;
        Date birthday;
        BigDecimal bd;
        Map<String, Object> map;
        List<DataVo> list;
    }

    public DataVo getDataVo(int id, String name) {
        // 初始化数据
        DataVo dataVo = new DataVo();
        dataVo.setId(id);
        dataVo.setName(name);
        dataVo.setBirthday(new Date());
        dataVo.setBd(new BigDecimal(10 + id));
        Map<String, Object> map = new HashMap<>();
        map.put("id", 20 + id);
        map.put("name", id + "_" + name);
        map.put("birthday", new Date());
        map.put("bd", new BigDecimal(20 + id));
        map.put("object", dataVo);
        dataVo.setMap(map);

        return dataVo;
    }


    @Test
    void getValue() {
        DataVo dataVo = getDataVo(0, "第一");
        List<DataVo> list = new ArrayList<>();
        dataVo.setList(list);

        DataVo item = getDataVo(1, "2");
        list.add(item);
        item = getDataVo(2, "3");
        list.add(item);

        log.debug("dataVo = {}", JSONObject.toJSONString(dataVo));
        // {"bd":10,"birthday":1740977627592,"id":0,
        // "list":[{"bd":11,"birthday":1740977627592,"id":1
        //          ,"map":{"birthday":1740977627592,"bd":21,"name":"1_2","id":21}
        //          ,"name":"2"}
        //        ,{"bd":12,"birthday":1740977627592,"id":2
        //         ,"map":{"birthday":1740977627592,"bd":22,"name":"2_3","id":22}
        //         ,"name":"3"
        //         }]
        // ,"map":{"birthday":1740977627592,"bd":20,"name":"0_第一","id":20}
        // ,"name":"第一"}

        log.debug("=========================================");
        String filed = "id";
        Object value = Clazz.getValue(dataVo, filed, false);
        log.debug("id:{}", value);
        Assertions.assertEquals(dataVo.getId(), value, "id == " + dataVo.getId());
        filed = "name";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("name:{}", value);
        Assertions.assertEquals(dataVo.getName(), value, "name == " + dataVo.getName());

        log.debug("=================Map========================");
        filed = "map[id]";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("map[id]:{}", value);
        Assertions.assertEquals(dataVo.getMap().get("id"), value, "map[id] == " + dataVo.getMap().get("id"));

        filed = "map[bd]";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("map[bd]:{}", value);
        Assertions.assertEquals(dataVo.getMap().get("bd"), value, "map[bd] == " + dataVo.getMap().get("bd"));

        filed = "map[birthday]";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("map[birthday][{}]:{}", value.getClass(), value);

        filed = "map[object].id";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("map[object].id:{}", value);

        filed = "map[object].map[birthday]";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("map[object].map[birthday]:{}", value);

        log.debug("=================List========================");
        filed = "list[0].id";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("list[0].id:{}", value);
        Assertions.assertEquals(dataVo.getList().get(0).id, value, "list[0].id == " + dataVo.getList().get(0).id);

        filed = "list[0].map[bd]";
        value = Clazz.getValue(dataVo, filed, false);
        log.debug("list[0].map[bd]:{}", value);

    }
}
