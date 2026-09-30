package cn.jbolt.admin.siargo.qarep;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.dictionary.DictionaryTypeService;
import cn.jbolt.core.model.Dictionary;
import cn.jbolt.core.model.DictionaryType;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.math.BigInteger;

/** 报告单内新增流量范围，仅允许维护固定字典类型的一条根级选项。 */
public class QarepFlowRangeService extends DictionaryService {

    private static final String TYPE_KEY = "siargo_flow_range";
    private static final int MAX_FIELD_LENGTH = 40;

    @Inject
    private DictionaryTypeService dictionaryTypeService;

    /** 建议下一个数字编码；超过字段长度时留空，由用户手动填写。 */
    public String suggestNextSn() {
        BigInteger max = BigInteger.ZERO;
        // 不带 enable 参数的原生查询包含停用项，避免建议已被停用选项占用的编码。
        for (Dictionary dictionary : getListByTypeKey(TYPE_KEY)) {
            String sn = dictionary.getStr("sn");
            if (StrKit.isBlank(sn)) {
                continue;
            }
            sn = sn.trim();
            if (sn.matches("[0-9]+")) {
                max = max.max(new BigInteger(sn));
            }
        }
        String next = max.add(BigInteger.ONE).toString();
        return next.length() <= MAX_FIELD_LENGTH ? next : "";
    }

    public Ret saveFlowRange(String name, String sn) {
        if (StrKit.isBlank(name)) {
            return fail("请输入流量范围名称");
        }
        if (StrKit.isBlank(sn)) {
            return fail("请输入流量范围编码");
        }
        name = name.trim();
        sn = sn.trim();
        if (name.length() > MAX_FIELD_LENGTH) {
            return fail("流量范围名称不能超过40个字符");
        }
        if (sn.length() > MAX_FIELD_LENGTH) {
            return fail("流量范围编码不能超过40个字符");
        }

        DictionaryType type = findFlowRangeType();
        if (type == null || notOk(type.getLong("id"))) {
            return fail("流量范围字典类型不存在，请联系管理员");
        }
        if (!Boolean.TRUE.equals(type.getBoolean("enable"))) {
            return fail("流量范围字典类型已停用，请联系管理员");
        }

        Dictionary dictionary = new Dictionary()
                .set("name", name)
                .set("sn", sn)
                .set("type_id", type.getLong("id"))
                .set("type_key", TYPE_KEY)
                .set("pid", 0L)
                .set("enable", true)
                .set("is_build_in", false);

        // true 表示单条保存，避免名称内的空格触发平台的批量新增分支。
        // 原生方法仅执行一条 INSERT，使用自动提交；Model 在保存完成后清字典缓存。
        // 不叠加外层事务，避免原生缓存失效发生在事务真正提交之前。
        return save(dictionary, true);
    }

    protected DictionaryType findFlowRangeType() {
        return dictionaryTypeService.findByTypeKey(TYPE_KEY);
    }
}
