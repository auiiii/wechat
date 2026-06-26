package com.zj.wechat.service.sportal;

import com.zj.wechat.dto.UserProfileVO;
import com.zj.wechat.entity.sportal.SpUser;
import com.zj.wechat.entity.sportal.SpUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /** 昵称最大长度，与 sp_users.nickname 列容量保持裕度，前端展示也更稳定 */
    private static final int NICKNAME_MAX_LENGTH = 32;
    /** 头像 URL 最大长度，对齐 sp_users.avatar 列容量 */
    private static final int AVATAR_MAX_LENGTH = 512;

    @Resource
    private SpUserDao spUserDao;

    public UserProfileVO getProfile(Long userId) {
        SpUser user = spUserDao.queryById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setFeedCount(spUserDao.countFeedsByUserId(userId));
        vo.setCheckinCount(spUserDao.countCheckinsByUserId(userId));
        vo.setLikeCount(spUserDao.countLikesReceivedByUserId(userId));
        vo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return vo;
    }

    /**
     * 更新用户资料，支持部分更新：
     * <ul>
     *   <li>未传 / 空白字符串 视为「不更新该字段」，仅传昵称或仅传头像均可。</li>
     *   <li>必须至少更新一个字段，否则会拼出 {@code UPDATE sp_users SET WHERE id=?} 非法 SQL。</li>
     *   <li>字段先 trim 再校验，避免落库前后空格。</li>
     * </ul>
     * 注意：当前不支持「把字段清空」语义（前端传空串会被视为不更新）。
     * 若后续业务需要支持显式清空，可改用 Optional 或独立 PATCH 接口。
     */
    @Transactional
    public UserProfileVO updateProfile(Long userId, String nickname, String avatar) {
        String normalizedNickname = trimToNull(nickname);
        String normalizedAvatar = trimToNull(avatar);

        if (normalizedNickname == null && normalizedAvatar == null) {
            throw new IllegalArgumentException("请至少修改一个字段（昵称或头像）");
        }
        if (normalizedNickname != null && normalizedNickname.length() > NICKNAME_MAX_LENGTH) {
            throw new IllegalArgumentException("昵称长度不能超过" + NICKNAME_MAX_LENGTH + "位");
        }
        if (normalizedAvatar != null && normalizedAvatar.length() > AVATAR_MAX_LENGTH) {
            throw new IllegalArgumentException("头像URL长度不能超过" + AVATAR_MAX_LENGTH + "位");
        }

        SpUser user = spUserDao.queryById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        SpUser update = new SpUser();
        update.setId(userId);
        update.setNickname(normalizedNickname);
        update.setAvatar(normalizedAvatar);
        spUserDao.updateProfile(update);

        return getProfile(userId);
    }

    /** trim 后为空串则返回 null，统一表达「未提供该字段」。 */
    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
