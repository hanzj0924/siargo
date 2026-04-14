# Siargo 订单检验状态查询 API 使用说明

## 一、认证方式

本接口采用 **应用认证（app_id）+ Token 签名（app_secret）** 双重验证机制：

- **app_id**：标识调用应用，由 JBolt 框架验证应用合法性（应用存在且已启用）
- **app_secret**：用于生成请求 Token 的签名密钥，由调用方本地计算，不在网络中传输

### 应用凭据

| 凭据       | 值                                             | 用途                        |
|------------|------------------------------------------------|-----------------------------|
| AppId      | `jbzvdqh9pxxmolt`                              | 每次请求作为 `jboltappid` 参数传递 |
| AppSecret  | `bjVzamw0ZWIzdjRsc3hmZGVwcmxta3RqOHc4OXZkaGM=` | 用于本地生成 Token 的签名密钥      |

> **安全提示**：AppSecret 是签名密钥，请妥善保管，切勿泄露、切勿在前端代码或公共仓库中暴露。

---

## 二、接口信息

### 2.1 单个订单查询

| 项目     | 说明                                       |
|----------|--------------------------------------------|
| 地址     | `/api/siargo/order/status`                 |
| 方式     | `GET`                                      |
| 认证     | `jboltappid` + `token`（URL 参数）         |

**请求参数：**

| 参数名     | 类型   | 必填 | 说明                                    |
|------------|--------|------|-----------------------------------------|
| jboltappid | String | 是   | 应用标识，固定值 `jbzvdqh9pxxmolt`      |
| orderId    | String | 是   | 订单号                                  |
| token      | String | 是   | 使用 AppSecret 生成的签名 Token         |

### 2.2 批量订单查询

| 项目     | 说明                                       |
|----------|--------------------------------------------|
| 地址     | `/api/siargo/order/batchStatus`            |
| 方式     | `GET`                                      |
| 认证     | `jboltappid` + `token`（URL 参数）         |

**请求参数：**

| 参数名     | 类型   | 必填 | 说明                                          |
|------------|--------|------|-----------------------------------------------|
| jboltappid | String | 是   | 应用标识，固定值 `jbzvdqh9pxxmolt`            |
| orderIds   | String | 是   | 多个订单号用逗号分隔，最多 100 个             |
| token      | String | 是   | 使用 AppSecret 对批量订单号生成的签名 Token   |

---

## 三、Token 生成算法

### 3.1 单个查询 Token

```
token = SHA256(AppSecret + orderId + YYYYMMDD)
```

- `AppSecret`：应用密钥 `bjVzamw0ZWIzdjRsc3hmZGVwcmxta3RqOHc4OXZkaGM=`
- `orderId`：订单号
- `YYYYMMDD`：当天日期，如 `20260414`
- 结果为 **64 位十六进制小写字符串**

### 3.2 批量查询 Token

```
token = SHA256(AppSecret + 排序后订单号逗号拼接 + YYYYMMDD)
```

- 将所有订单号**按字符串排序**后用逗号 `,` 拼接
- 其余规则与单个查询相同

### 3.3 Token 有效期

- Token 当天有效（按服务器日期计算）
- 服务端同时验证当天和前一天的 Token，解决跨日边界问题

---

## 四、代码示例

### 4.1 Java 示例

```java
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SiargoApiClient {

    private static final String APP_ID = "jbzvdqh9pxxmolt";
    private static final String APP_SECRET = "bjVzamw0ZWIzdjRsc3hmZGVwcmxta3RqOHc4OXZkaGM=";
    private static final String BASE_URL = "http://your-server/api/siargo/order";

    /**
     * 生成单个查询Token
     */
    public static String generateToken(String orderId) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String raw = APP_SECRET + orderId + dateStr;
        return sha256Hex(raw);
    }

    /**
     * 生成批量查询Token
     */
    public static String generateBatchToken(String[] orderIds) {
        java.util.Arrays.sort(orderIds);
        String joined = String.join(",", orderIds);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return sha256Hex(APP_SECRET + joined + dateStr);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256计算失败", e);
        }
    }

    // 调用示例
    public static void main(String[] args) {
        // 单个查询
        String orderId = "103697";
        String token = generateToken(orderId);
        String url = BASE_URL + "/status?jboltappid=" + APP_ID
                + "&orderId=" + orderId + "&token=" + token;
        System.out.println("单个查询URL: " + url);

        // 批量查询
        String[] orderIds = {"103697", "103741", "103773"};
        String batchToken = generateBatchToken(orderIds);
        String batchUrl = BASE_URL + "/batchStatus?jboltappid=" + APP_ID
                + "&orderIds=" + String.join(",", orderIds) + "&token=" + batchToken;
        System.out.println("批量查询URL: " + batchUrl);
    }
}
```

### 4.2 Python 示例

```python
import hashlib
import datetime
import requests

APP_ID = "jbzvdqh9pxxmolt"
APP_SECRET = "bjVzamw0ZWIzdjRsc3hmZGVwcmxta3RqOHc4OXZkaGM="
BASE_URL = "http://your-server/api/siargo/order"

def generate_token(order_id):
    """生成单个查询Token"""
    date_str = datetime.date.today().strftime("%Y%m%d")
    raw = APP_SECRET + order_id + date_str
    return hashlib.sha256(raw.encode('utf-8')).hexdigest()

def generate_batch_token(order_ids):
    """生成批量查询Token"""
    sorted_ids = sorted(order_ids)
    joined = ",".join(sorted_ids)
    date_str = datetime.date.today().strftime("%Y%m%d")
    return hashlib.sha256((APP_SECRET + joined + date_str).encode('utf-8')).hexdigest()

# 单个查询
order_id = "103697"
token = generate_token(order_id)
resp = requests.get(f"{BASE_URL}/status", params={
    "jboltappid": APP_ID,
    "orderId": order_id,
    "token": token
})
print(resp.json())

# 批量查询
order_ids = ["103697", "103741", "103773"]
batch_token = generate_batch_token(order_ids)
resp = requests.get(f"{BASE_URL}/batchStatus", params={
    "jboltappid": APP_ID,
    "orderIds": ",".join(order_ids),
    "token": batch_token
})
print(resp.json())
```

---

## 五、响应格式

### 5.1 单个查询 - 成功响应

当订单存在且包含产品检验数据时，返回 `data` 数组（一个订单可能关联多条产品记录）：

```json
{
    "status": "ok",
    "code": 200,
    "msg": "查询成功",
    "orderId": "103781",
    "data": [
        {
            "model": "MF5212-Q-300-A",
            "number": "G7HSH96001",
            "insp": 2,
            "accq_time": "2026-04-03 14:27:05",
            "funq_time": null,
            "appq_time": null,
            "allq_time": null,
            "accq_name": "韩子衿",
            "funq_name": null,
            "appq_name": null,
            "allq_name": null
        },
        {
            "model": "MF5219-Q-800-A",
            "number": "G7HSH96099",
            "insp": 2,
            "accq_time": "2026-04-03 14:28:07",
            "funq_time": null,
            "appq_time": null,
            "allq_time": null,
            "accq_name": "韩子衿",
            "funq_name": null,
            "appq_name": null,
            "allq_name": null
        }
    ]
}
```

### 5.2 单个查询 - 失败响应

```json
{
    "status": "fail",
    "code": 1004,
    "msg": "订单未创建",
    "orderId": "999999",
    "insp": 0
}
```

### 5.3 批量查询 - 成功响应

```json
{
    "status": "ok",
    "code": 200,
    "msg": "查询成功",
    "total": 3,
    "results": [
        {
            "orderId": "103737",
            "data": [
                {
                    "model": "MF5212-Q-300-A",
                    "number": "G7HSH96001",
                    "insp": 5,
                    "accq_time": "2026-03-01 10:30:00",
                    "funq_time": "2026-03-02 14:20:00",
                    "appq_time": "2026-03-03 09:00:00",
                    "allq_time": "2026-03-04 16:00:00",
                    "accq_name": "张三",
                    "funq_name": "李四",
                    "appq_name": "王五",
                    "allq_name": "赵六"
                }
            ]
        },
        {
            "orderId": "103781",
            "data": [
                {
                    "model": "MF5219-Q-800-A",
                    "number": "G7HSH96099",
                    "insp": 2,
                    "accq_time": "2026-04-03 14:28:07",
                    "funq_time": null,
                    "appq_time": null,
                    "allq_time": null,
                    "accq_name": "韩子衿",
                    "funq_name": null,
                    "appq_name": null,
                    "allq_name": null
                }
            ]
        },
        {
            "orderId": "999999",
            "insp": 0,
            "msg": "订单未创建"
        }
    ]
}
```

---

## 六、字段说明

### 6.1 顶层字段

| 字段名  | 类型   | 说明                                         |
|---------|--------|----------------------------------------------|
| status  | String | 请求结果：`ok` 成功，`fail` 失败             |
| code    | int    | 状态码（成功时 `200`，失败时见状态码说明）   |
| msg     | String | 提示消息（成功或失败原因）                   |
| orderId | String | 请求的订单号（单个查询时返回）               |
| data    | Array  | 产品检验数据列表（仅成功时返回）             |
| insp    | int    | 检验进度（仅失败时返回，固定为 `0`）         |
| total   | int    | 结果数量（仅批量查询时返回）                 |
| results | Array  | 批量查询结果数组（仅批量查询时返回）         |

### 6.2 data 数组中每条记录的字段

| 字段名    | 类型   | 说明                     |
|-----------|--------|------------------------|
| model     | String | 产品型号                 |
| number    | String | 产品编号/序列号          |
| insp      | int    | 检验进度（见下方枚举值） |
| accq_time | String | 精度检验时间             |
| funq_time | String | 功能检验时间             |
| appq_time | String | 外观检验时间             |
| allq_time | String | 最终审核时间             |
| accq_name | String | 精度检验人员姓名         |
| funq_name | String | 功能检验人员姓名         |
| appq_name | String | 外观检验人员姓名         |
| allq_name | String | 最终审核人员姓名         |

### 6.3 insp 检验进度枚举值

| 值 | 状态       | 说明                           |
|----|------------|--------------------------------|
| 0  | 无数据     | 订单未创建                     |
| 1  | 精度待检   | 未开始检验，等待精度检验       |
| 2  | 外观待检   | 精度检验已完成                 |
| 3  | 包装待检   | 功能检验已完成                 |
| 4  | 合格待批准 | 外观检验已完成，等待最终批准   |
| 5  | 已完成     | 最终审核通过，检验流程全部完成 |

---

## 七、状态码说明

| 状态码 | 说明                       | 触发场景                             |
|--------|----------------------------|--------------------------------------|
| 200    | 查询成功                   | 正常返回数据                         |
| 1001   | 订单号不能为空             | 未传 orderId / orderIds 参数         |
| 1002   | token 不能为空             | 未传 token 参数                      |
| 1003   | token 验证失败             | Token 计算错误或已过期               |
| 1004   | 订单未创建                 | 数据库中无该订单记录（单个查询）     |
| 1005   | 单次批量查询不能超过100个  | 批量查询订单数超过限制               |

> **注意**：如果 `jboltappid` 参数缺失或无效，框架会返回自身的错误响应（非上述格式），请确保每次请求都携带正确的 `jboltappid`。

---

## 八、完整调用流程

1. **获取凭据**：从管理员处获取 `AppId` 和 `AppSecret`
2. **生成 Token**：使用 `AppSecret` + `orderId` + 当天日期（YYYYMMDD）计算 SHA256
3. **发送请求**：将 `jboltappid`、`orderId`、`token` 作为 URL 参数发送 GET 请求
4. **解析响应**：根据 `status` 和 `code` 字段判断结果

### 完整请求示例

```
单个查询：
GET /api/siargo/order/status?jboltappid=jbzvdqh9pxxmolt&orderId=103697&token=a1b2c3...

批量查询：
GET /api/siargo/order/batchStatus?jboltappid=jbzvdqh9pxxmolt&orderIds=103697,103741,103773&token=d4e5f6...
```

---

## 九、注意事项

1. **凭据安全**：`AppSecret` 仅用于服务端 Token 计算，切勿在前端代码、日志、公共仓库中暴露
2. **Token 时效**：Token 基于日期生成，当天有效（服务端兼容前一天 Token 以处理跨日边界）
3. **批量查询限制**：单次批量查询最多 100 个订单号
4. **Token 不可复用**：不同订单号的 Token 不同，批量查询的 Token 与单个查询的 Token 不同
5. **订单号排序**：批量查询时，Token 计算需要将订单号按字符串排序后再拼接
6. **并发安全**：Token 生成算法是无状态的，可安全用于多线程环境
7. **编码要求**：字符串拼接和 SHA256 计算统一使用 UTF-8 编码
