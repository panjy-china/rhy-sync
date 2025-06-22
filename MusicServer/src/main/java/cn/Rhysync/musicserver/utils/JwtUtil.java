package cn.Rhysync.musicserver.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类 (重构优化版)
 *
 * <p>
 * 主要优化点:
 * 1.  **正确的密钥生成**: 使用 `Keys.hmacShaKeyFor` 根据 `HS256` 算法安全地生成密钥，取代了原先错误的 "AES" 算法声明。
 * 2.  **提升性能**: 将密钥 `SecretKey` 缓存为静态常量，在类加载时仅生成一次，避免了每次调用时的重复计算。
 * 3.  **健壮的异常处理**: `isExpiration` 方法现在能正确处理各种JWT异常，明确区分过期和其他无效情况，逻辑更清晰可靠。
 * 4.  **代码现代化**: 使用 `jjwt` 库推荐的 `Jwts.parserBuilder()` API 进行解析。
 * </p>
 *
 * @Author XiaoMo
 * @Date 2022/7/11 9:44
 * @Version 2.0
 */
public class JwtUtil {
    // 请求头里面的token的key
    public static final String TOKEN_HEADER = "Authorization";
    // JWT令牌前缀 表示持有人
    public static final String TOKEN_PREFIX = "Bearer ";
    // 有效期，默认为一个小时
    public static final Long JWT_TTL = 60 * 60 * 1000L; // 60 * 60 * 1000  一个小时

    // 设置密钥明文。
    // !! 警告: 在生产环境中，密钥应更复杂，并存储在安全的配置文件或环境变量中，而不是硬编码在代码里。
    private static final String JWT_KEY = "a-much-longer-and-more-secure-secret-key-for-production-use";

    // 根据密钥明文生成一个安全的SecretKey对象，该对象在类加载时只生成一次。
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成一个随机的UUID作为JWT的ID
     * @return 随机UUID字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成jwt
     * 使用默认过期时间
     *
     * @param subject token中要存放的数据（通常是用户ID或其他标识符）
     * @return 生成的JWT字符串
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    /**
     * 生成jwt
     *
     * @param subject   token中要存放的数据（通常是用户ID或其他标识符）
     * @param ttlMillis token超时时间（毫秒）
     * @return 生成的JWT字符串
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());
        return builder.compact();
    }

    /**
     * 创建token
     *
     * @param id        JWT的唯一ID
     * @param subject   token中要存放的数据（通常是用户ID或其他标识符）
     * @param ttlMillis token超时时间（毫秒）
     * @return 生成的JWT字符串
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);
        return builder.compact();
    }

    /**
     * 内部方法，用于构建JwtBuilder
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        return Jwts.builder()
                .setId(uuid)              // 唯一的ID
                .setSubject(subject)      // 主题, 可以是JSON数据
                .setIssuer("Rhysync")     // 签发者 (建议使用项目或公司标识)
                .setIssuedAt(now)         // 签发时间
                .signWith(SECRET_KEY)     // 使用HS256对称加密算法签名, 传入缓存的SecretKey
                .setExpiration(expDate);  // 设置过期时间
    }


    /**
     * 解析JWT
     *
     * @param jwt JWT字符串
     * @return JWT中的Claims（负载）
     * @throws JwtException 如果解析失败（包括过期、签名无效、格式错误等），则抛出异常
     */
    public static Claims parseJWT(String jwt) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 设置签名密钥
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 检查token是否已过期
     * <p>
     * 注意：此方法将任何形式的验证失败（如签名错误、格式错误）都视为“已过期”或“不可用”。
     * 这是一个安全的选择，因为任何无效的token都应被拒绝。
     * </p>
     *
     * @param token JWT Token
     * @return 如果token已过期或无效，则返回true；否则返回false。
     */
    public static boolean isExpiration(String token) {
        try {
            // parseJWT方法在token过期、签名错误或格式不正确时会抛出异常
            parseJWT(token);
            // 如果没有抛出异常，说明token是有效的且未过期
            return false;
        } catch (ExpiredJwtException e) {
            // Token已过期，这是预期的异常
            return true;
        } catch (JwtException e) {
            // 其他所有JwtException（如SignatureException, MalformedJwtException等）
            // 都表明token是无效的。从安全角度出发，我们将无效token也视为“过期”处理。
            // 可以在此处添加日志记录 e.getMessage() 来进行调试
            return true;
        }
    }

    public static void main(String[] args) {
        // 创建一个JWT
        String subject = "user-123";
        String token = createJWT(subject);
        System.out.println("生成的Token: " + token);
        System.out.println("Token是否过期: " + isExpiration(token));

        // 解析Token
        try {
            Claims claims = parseJWT(token);
            System.out.println("解析出的Subject: " + claims.getSubject());
            System.out.println("解析出的ID: " + claims.getId());
            System.out.println("签发者: " + claims.getIssuer());
        } catch (Exception e) {
            System.err.println("解析失败: " + e.getMessage());
        }
    }
}