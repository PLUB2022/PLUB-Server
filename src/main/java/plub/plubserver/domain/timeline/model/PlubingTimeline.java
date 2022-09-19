package plub.plubserver.domain.timeline.model;

import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubing.model.Plubing;
import plub.plubserver.domain.todo.model.PlubingTodo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PlubingTimeline extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeline_id")
    private Long id;

    private int totalLikes;
    private int totalComments;

    // 타임라인(다) - 모임(1)
    @ManyToOne
    @JoinColumn(name = "plubing_id")
    private Plubing plubing;

    // 타임라인(1) - 투두(다)
    @OneToMany(mappedBy = "timeLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubingTodo> todoList = new ArrayList<>();

}
