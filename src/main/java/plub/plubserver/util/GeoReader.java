package plub.plubserver.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Subdivision;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;


@Component
@Slf4j
public class GeoReader {
    private DatabaseReader reader;

    public GeoReader() {
        try {
            ClassPathResource resource = new ClassPathResource("GeoLite2-City.mmdb");
            File dbFile = new File(resource.getURI());
            reader = new DatabaseReader.Builder(dbFile).build();
        } catch (Exception ex) {
            log.warn("GeoReader init error : {}", ex.getMessage());
        }
    }

    public String getCity(InetAddress IpAddress) {
        try {
            CityResponse response = reader.city(IpAddress);
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();
            return city.getName() + "," + subdivision.getName();
        } catch (Exception ex) {
            return "unknown";
        }
    }

}
