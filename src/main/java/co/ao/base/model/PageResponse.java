package co.ao.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int number;

    public List<T> getContent() {
        return content;
    }
    private int size;
    private long totalElements;
    private int totalPages;

    public long getTotalElements() {
        return totalElements;
    }
    private boolean first;
    private boolean last;
}
