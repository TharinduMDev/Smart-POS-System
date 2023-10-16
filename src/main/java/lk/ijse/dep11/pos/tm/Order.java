package lk.ijse.dep11.pos.tm;

import com.jfoenix.controls.JFXButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.function.BiConsumer;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String code;
    private String description;
    private int qty;
    private BigDecimal unitPrice;
    private JFXButton btnDelete;
}
