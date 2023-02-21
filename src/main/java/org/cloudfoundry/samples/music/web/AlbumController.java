package org.cloudfoundry.samples.music.web;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.cloudfoundry.samples.music.domain.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Random;
import java.util.function.Supplier;

@RestController
@RequestMapping(value = "/albums")
public class AlbumController {
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);
    private CrudRepository<Album, String> repository;


    // Guess: As the constructor, it has the same name as the class itself: "AlbumController."
    // It creates "AlbumController" objects.
    @Autowired
    public AlbumController(CrudRepository<Album, String> repository, MeterRegistry registry) {
        this.repository = repository;
        Gauge.builder("albumcontroller.randomvalue",fetchRandomValue()).
                tag("version","v1").
                tag("origin","albumorigin").
                tag("job","albumjob").
                tag("source_id","albumsource").
                tag("album_tag","freshcuts").
                description("albumcontroller descrip").
                register(registry);
    }

    // A Supplier<Number> reminds me of a reference to a function.
    // Guess: The Gauge.builder() function takes Supplier, which it invokes using a get() at a different point in time.
    public Supplier<Number> fetchRandomValue() {
         Supplier<Number> randomFloatSupplier = () -> new Random().nextFloat();
         return randomFloatSupplier;
    }
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Album> albums() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Album add(@RequestBody @Valid Album album) {
        logger.info("Adding album " + album.getId());
        return repository.save(album);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Album update(@RequestBody @Valid Album album) {
        logger.info("Updating album " + album.getId());
        return repository.save(album);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Album getById(@PathVariable String id) {
        logger.info("Getting album " + id);
        return repository.findById(id).orElse(null);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting album " + id);
        repository.deleteById(id);
    }
}