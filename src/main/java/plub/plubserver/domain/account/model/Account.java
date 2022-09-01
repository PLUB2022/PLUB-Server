package plub.plubserver.domain.account.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private int age;
    private String birthday;
    private String gender;
    private String phone;
    private String provider;
    private String profile; // saved_path
    private String lastLogin;
    private String fcmToken;
}
