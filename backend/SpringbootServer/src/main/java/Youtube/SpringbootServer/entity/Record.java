package Youtube.SpringbootServer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Record {

    @Id
    @GeneratedValue
    @Column(name = "record_id")
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "record")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "record")
    private List<Keyword> keywords = new ArrayList<>();

    @OneToOne(mappedBy = "record", fetch = LAZY)
    private VideoInformation videoInformation;

    @JsonIgnore
    @OneToMany(mappedBy = "record")
    private List<Interest> interests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "record")
    private List<Timeline> timelines = new ArrayList<>();

    @ManyToOne( fetch = LAZY)
    @JoinColumn(name= "member_id")
    private Member member;

    @Column(name="created_date")
    @CreatedDate
    private String createDate;

    @PrePersist
    public void onPrePersist() {
        this.createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd a KK : mm",  Locale.KOREAN));
    }

    public void addMember(Member member){
        this.member = member;
        member.getRecords().add(this);
    }
}
