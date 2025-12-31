package jbolt;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentTest {
    
    // 测试目标接口URL
    private static final String TARGET_URL = "http://localhost:80/user";
    private static final int TOTAL_REQUESTS = 50;  // 总请求数
    private static final int CONCURRENT_THREADS = 50; // 并发线程数
    
    // 统计计数器
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);
    private static final AtomicLong totalResponseTime = new AtomicLong(0);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始并发测试...");
        System.out.println("目标URL: " + TARGET_URL);
        System.out.println("并发线程数: " + CONCURRENT_THREADS);
        System.out.println("总请求数: " + TOTAL_REQUESTS);
        
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        
        // 创建测试任务
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.submit(new ApiRequestTask(i, latch));
        }
        
        // 等待所有任务完成
        latch.await();
        executor.shutdown();
        
        // 输出统计结果
        printStatistics();
    }
    
    static class ApiRequestTask implements Runnable {
        private final int requestId;
        private final CountDownLatch latch;
        
        public ApiRequestTask(int requestId, CountDownLatch latch) {
            this.requestId = requestId;
            this.latch = latch;
        }
        
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            
            try {
                // 模拟HTTP请求
                String response = sendHttpRequest();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // 统计
                successCount.incrementAndGet();
                totalResponseTime.addAndGet(duration);
                
                System.out.printf("请求 %d 成功, 耗时: %d ms%n", requestId, duration);
                
            } catch (Exception e) {
                failureCount.incrementAndGet();
                System.err.printf("请求 %d 失败: %s%n", requestId, e.getMessage());
            } finally {
                latch.countDown();
            }
        }
        
        private String sendHttpRequest() throws Exception {
            // 使用HttpURLConnection发送请求
            java.net.URL url = new java.net.URL(TARGET_URL);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            
            // 设置请求参数
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            
            // 设置请求头
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "ConcurrentTest/1.0");
            
            // 发送POST数据
            String postData = String.format(
                "{\"username\":\"testuser%d\",\"password\":\"password123\"}",
                requestId % 10  // 使用10个测试账号循环
            );
            
            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes());
                os.flush();
            }
            
            // 获取响应
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            
            conn.disconnect();
            
            // 验证响应
            if (responseCode != 200) {
                throw new RuntimeException("HTTP响应码: " + responseCode);
            }
            
            return response.toString();
        }
    }
    
    private static void printStatistics() {
        int total = successCount.get() + failureCount.get();
        double successRate = (double) successCount.get() / total * 100;
        double avgResponseTime = (double) totalResponseTime.get() / successCount.get();
        
        System.out.println("\n========== 测试统计 ==========");
        System.out.println("总请求数: " + total);
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failureCount.get());
        System.out.printf("成功率: %.2f%%%n", successRate);
        System.out.printf("平均响应时间: %.2f ms%n", avgResponseTime);
        System.out.printf("QPS (请求/秒): %.2f%n", 
            (double) successCount.get() / (totalResponseTime.get() / 1000.0));
        System.out.println("=============================");
    }
}
