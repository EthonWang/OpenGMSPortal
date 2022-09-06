package njgis.opengms.portal.entity.doo.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class referenceSystem {
  temporalReference temporalReference;
  List<spatialReference> spatialReference;
}
