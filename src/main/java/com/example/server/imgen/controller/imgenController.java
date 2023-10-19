package com.example.server.imgen.controller;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
// import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
// import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;
import java.util.List;
// import java.util.Locale.Category;
import javax.imageio.ImageIO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.imgscalr.Scalr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.imgen.Utils.ImagePadder;
import com.example.server.imgen.Utils.ImgenCategories;
import com.example.server.imgen.pojo.ArtWork;
import com.example.server.imgen.service.IArtworkService;
import com.example.server.imgen.vo.ArtWorkVo;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import com.madgag.gif.fmsware.*;

@RestController
@RequestMapping("/imgen")
@Slf4j
public class imgenController {
    final String imgenPath   = "http://localhost:8080/imgen/";
    final String acquirePath = imgenPath + "acquire/";
    final String generateDir = "e://web_app/results/";
    final String drvvRsc     = "/drvv.jpg";
    final String drvvBgRsc   = "/vvbg.jpg";
    final String petpetRsc   = "/petpet-hand.gif";
    final String gigantaRsc  = "/giganta.gif";
    final String perfectRsc  = "/perfect.png";
    final String marryRsc    = "/marriage.png";
    final String brainRsc    = "/brain-upgrade.gif";

    final int [] categories  = {
        ImgenCategories.PETPET,
        ImgenCategories.GIGANTA,
        ImgenCategories.PERFECT,
        ImgenCategories.MARRY,
        ImgenCategories.BRAIN,
        ImgenCategories.DRVV,
    };

    @Autowired
    IArtworkService artwork;

    @GetMapping("/petpet")
    public ResponseEntity petpet(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        String imgUrl        = path + name;
        String gifName       = name + "-petpet.gif";
        File file            = new File(imgUrl);
        BufferedImage image  = ImageIO.read(file);
        image = Scalr.resize(image, 160);
        
        final Integer width        = image.getWidth();
        final Integer height       = image.getHeight();
        final Double step          = 0.1;
        final Double maxScaling    = 1 + step * 2;
        final Integer maxPadding   = (((Double) (width * maxScaling)).intValue() - width) / 2;
        final Integer maxWidth     = width + 2 * maxPadding;
        final Integer maxHeight    = height;
        Double currentWidthScaling = 1.;

        // final File gif = new File(outputFullPath);
        // final var out = new FileImageOutputStream(gif);
        // GifSequenceWriter gifSequenceWriter = new GifSequenceWriter(out, image.getType(), 0, true);
        final String userDir = generateDir + userMail + '/';
        final String outputFullPath = userDir + gifName;
        final File directory = new File(userDir);
        // log.info(userDir);

        if (!directory.exists()) {
            directory.mkdir();
        }

        var gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFullPath);
        gifEncoder.setDispose(3);
        gifEncoder.setRepeat(0);
        gifEncoder.setDelay(100);

        ImagePadder padder = new ImagePadder(maxWidth, maxHeight);

        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(getClass().getResourceAsStream(petpetRsc));
        int frameCount = gifDecoder.getFrameCount();
        ArrayList<BufferedImage> resizeds = new ArrayList<>();

        for (int i = 0; i < 3; ++i)
        {
            Double currentHeightScaling = 1. / currentWidthScaling;
            Integer currentWidth  = ((Double) (width * currentWidthScaling)).intValue();
            Integer currentHeight = ((Double) (height * currentHeightScaling)).intValue();

            resizeds.add(padder.pad(image, currentWidth, currentHeight));

            currentWidthScaling += step;
        }

        for (int i = 0; i < 2; ++i)
        {
            currentWidthScaling -= step;
            Double currentHeightScaling = 1. / currentWidthScaling;
            Integer currentWidth  = ((Double) (width * currentWidthScaling)).intValue();
            Integer currentHeight = ((Double) (height * currentHeightScaling)).intValue();

            resizeds.add(padder.pad(image, currentWidth, currentHeight));
        }

        for (int i = 0; i < frameCount; ++i)
        {
            var combined = padder.combine(resizeds.get(i), gifDecoder.getFrame(i));
            // gifSequenceWriter.writeToSequence(combined);
            gifEncoder.addFrame(combined);
        }

        // gifSequenceWriter.close();
        gifEncoder.finish();

        saveArtwork(userMail, gifName, by, ImgenCategories.PETPET);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(gifName);
    }

    @GetMapping("/perfect")
    public ResponseEntity perfect(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        String imgUrl         = path + name;
        String imgName        = name + "-perfect.png";
        File   file           = new File(imgUrl);
        BufferedImage image   = ImageIO.read(file);
        BufferedImage perfect = ImageIO.read(getClass().getResourceAsStream(perfectRsc));
        image = Scalr.resize(image, 313, 459);

        final Integer imw = image.getWidth();
        final Integer imh = image.getHeight();
        final Integer pfw = perfect.getWidth();
        final Integer pfh = perfect.getHeight();
        
        final Integer x = 309;
        final Integer y = 65;
        
        BufferedImage combined = new BufferedImage(652, 524, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(perfect, 0, 0, null);
        g.drawImage(image, x, y + (pfh - imh) / 2, null);
        
        final String userDir = generateDir + userMail + '/';
        final String outputFullPath = userDir + imgName;
        final File directory = new File(userDir);
        
        if (!directory.exists()) {
            directory.mkdir();
        }

        ImageIO.write(combined, "png", new File(outputFullPath));

        saveArtwork(userMail, imgName, by, ImgenCategories.PERFECT);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(imgName);
    }

    @GetMapping("/marry")
    public ResponseEntity marry(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        String imgUrl         = path + name;
        String imgName        = name + "-marry.png";
        File   file           = new File(imgUrl);
        BufferedImage image   = ImageIO.read(file);
        BufferedImage marry   = ImageIO.read(getClass().getResourceAsStream(marryRsc));
        image = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, 800, 1080);
        
        BufferedImage combined = new BufferedImage(800, 1080, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(marry, 0, 0, null);
        
        final String userDir = generateDir + userMail + '/';
        final String outputFullPath = userDir + imgName;
        final File directory = new File(userDir);
        
        if (!directory.exists()) {
            directory.mkdir();
        }

        ImageIO.write(combined, "png", new File(outputFullPath));

        saveArtwork(userMail, imgName, by, ImgenCategories.MARRY);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(imgName);
    }

    @GetMapping("/giganta")
    public ResponseEntity giganta(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        String imgUrl        = path + name;
        String gifName       = name + "-giganta.gif";
        File   file          = new File(imgUrl);
        BufferedImage image  = ImageIO.read(file);
        image = Scalr.resize(image, 200);
        
        final Integer width        = image.getWidth();
        final Integer height       = image.getHeight();
        final Double  step         = 0.05;
        final Double  maxScaling   = 1 + step * 3;
        final Integer maxPadding   = (((Double) (width * maxScaling)).intValue() - width) / 2;
        final Integer maxWidth     = width + 2 * maxPadding;
        final Integer maxHeight    = height;
        Double currentWidthScaling = 1.;

        // var gif = new File(generateDir + gifName);
        // final var out = new FileImageOutputStream(gif);
        // GifSequenceWriter gifSequenceWriter = new GifSequenceWriter(out, image.getType(), 0, true);
        final String userDir = generateDir + userMail + '/';
        final String outputFullPath = userDir + gifName;
        final File directory = new File(userDir);

        if (!directory.exists()) {
            directory.mkdir();
        }

        var gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFullPath);
        gifEncoder.setDispose(3);
        gifEncoder.setRepeat(0);
        gifEncoder.setDelay(100);

        // initialize image padder
        ImagePadder padder = new ImagePadder(maxWidth, maxHeight);

        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(getClass().getResourceAsStream(gigantaRsc));
        int stillAlive    = 15;
        int frameCount    = gifDecoder.getFrameCount();
        Integer offset    = maxWidth / 2;
        Integer pieWidth  = maxWidth;
        Integer pieHeight = ((Double) (maxHeight * 0.3)).intValue();
        
        log.info("count={}", frameCount);
        
        BufferedImage pie = padder.pad(image, pieWidth, pieHeight);

        ArrayList<BufferedImage> resizeds = new ArrayList<>();

        for (int i = 0; i < 4; ++i)
        {
            Double currentHeightScaling = 1. / currentWidthScaling;
            Integer currentWidth  = ((Double) (width * currentWidthScaling)).intValue();
            Integer currentHeight = ((Double) (height * currentHeightScaling)).intValue();

            resizeds.add(padder.pad(image, currentWidth, currentHeight));

            currentWidthScaling += step;
        }

        for (int i = 0; i < 3; ++i)
        {
            currentWidthScaling -= step;
            Double currentHeightScaling = 1. / currentWidthScaling;
            Integer currentWidth  = ((Double) (width * currentWidthScaling)).intValue();
            Integer currentHeight = ((Double) (height * currentHeightScaling)).intValue();

            resizeds.add(padder.pad(image, currentWidth, currentHeight));
        }

        Integer cucumberX = 118;
        Integer cucumberY = 170;

        for (int i = 0; i < stillAlive; ++i)
        {
            // String outputDir = "e://output/";
            // File output = new File(outputDir + i + "gg.png");
            // if (!output.exists())
            // {
            //     output.createNewFile();
            // }
            // ImageIO.write(gifDecoder.getFrame(i), "png", output);

            // cucumberX - offset for central alignment
            var combined = padder.combinedTo(gifDecoder.getFrame(i), resizeds.get(i % 7), cucumberX - offset, cucumberY);
            // gifSequenceWriter.writeToSequence(combined);
            gifEncoder.addFrame(combined);
        }

        for (int i = stillAlive; i < frameCount; ++i)
        {
            var combined = padder.combinedTo(gifDecoder.getFrame(i), pie, cucumberX - offset, cucumberY);
            // gifSequenceWriter.writeToSequence(combined);
            gifEncoder.addFrame(combined);
        }

        // gifSequenceWriter.close();
        gifEncoder.finish();

        saveArtwork(userMail, gifName, by, ImgenCategories.GIGANTA);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(gifName);
    }

    @GetMapping("/brain")
    public ResponseEntity brain(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        String imgUrl        = path + name;
        String gifName       = name + "-brain.gif";
        File   file          = new File(imgUrl);
        BufferedImage image  = ImageIO.read(file);

        // var gif = new File(generateDir + gifName);
        // final var out = new FileImageOutputStream(gif);
        // GifSequenceWriter gifSequenceWriter = new GifSequenceWriter(out, image.getType(), 0, true);
        final String userDir = generateDir + userMail + '/';
        final String outputFullPath = userDir + gifName;
        final File directory = new File(userDir);

        if (!directory.exists()) {
            directory.mkdir();
        }

        var gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputFullPath);
        gifEncoder.setDispose(3);
        gifEncoder.setRepeat(0);
        gifEncoder.setDelay(100);

        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(getClass().getResourceAsStream(brainRsc));
        Integer brainWidth  = gifDecoder.getFrame(0).getWidth();
        Integer brainHeight = gifDecoder.getFrame(0).getHeight();
        image = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, brainWidth, brainHeight);
        final Integer width        = image.getWidth();
        final Integer height       = image.getHeight();

        int frameCount    = gifDecoder.getFrameCount();
        Integer offset    = (brainWidth - width) / 2;
                
        BufferedImage transparentImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = (Graphics2D) transparentImg.getGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
        tg.setComposite(ac);
        tg.drawImage(image, 0, 0, null);

        for (int i = 0; i < frameCount; ++i)
        {
            BufferedImage combined = new BufferedImage(brainWidth, brainHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();
            g.drawImage(gifDecoder.getFrame(i), 0, 0, null);
            g.drawImage(transparentImg, offset, 0, null);
            gifEncoder.addFrame(combined);
        }

        // gifSequenceWriter.close();
        gifEncoder.finish();

        saveArtwork(userMail, gifName, by, ImgenCategories.BRAIN);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(gifName);
    }

    @GetMapping("/last5")
    public ResponseEntity last5()
    {
        QueryWrapper<ArtWork> query = new QueryWrapper<>();
        query.orderByDesc("id").last("limit 5");
        // Page<ArtWork> page = new Page<ArtWork>(0, 5);
        List<ArtWork> arts = artwork.list(query);
        List<ArtWorkVo> artVos = new ArrayList<>();
        String[] labels = {
            ">.*-Y",
            "Grüß dich!",
            "Ciao~",
            "Servus!",
            "Ohayo~"
        };
        int i = 0;
        
        for (var art: arts) {
            ArtWorkVo artVo = new ArtWorkVo();
            artVo.setLabel(labels[i]);
            artVo.setPath(acquirePath + art.getEmail() + '/' + art.getName());
            log.info(art.getIllustrator());
            artVo.setIllustrator(art.getIllustrator());
            artVos.add(artVo);
            ++i;
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(artVos);
    }

    @GetMapping("/imglist")
    public ResponseEntity imglist(
        @RequestParam(required = false, defaultValue = "1") int pageCount,
        @RequestParam(required = false, defaultValue = "63") int category,
        @RequestParam(required = false, defaultValue = "9") int pageSize,
        @RequestParam(required = false, defaultValue = "") String userMail,
        @RequestParam(required = false, defaultValue = "false") boolean revert
    )
    {
        QueryWrapper<ArtWork> query = new QueryWrapper<>();

        List<Integer> inputCategories = new ArrayList<>();
        
        if (userMail.length() > 0) {
            log.info(userMail);
            // log.info("{}", userMail.length());
            // query.and(wrapper -> wrapper.eq("email", userMail));
            query.eq("email", userMail);
        }

        if (category != ImgenCategories.ALL && category != 0) {
            for (int _category : categories) {
                if ((category &  _category) != 0) {
                    inputCategories.add(_category);
                }
            }
        } else if (category == 0) {
            query.eq("category", 0);
        }

        if (inputCategories.size() != 0) {
            query.in("category", inputCategories);
        }

        if (revert == true) {
            query.orderByDesc("id");
        }

        // log.info(query.getTargetSql());

        Page<ArtWork> page = new Page<ArtWork>(pageCount, pageSize);
        var arts = artwork.page(page, query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(arts);
    }
    
    @GetMapping(value = "/acquire/{userMail}/{gif}", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] acquire(@PathVariable("userMail") String userMail, @PathVariable("gif") String gif) throws IOException {
        final String userDir = generateDir + userMail + '/';
        byte[] bytes = Files.readAllBytes(Paths.get(userDir + gif));

        return bytes;
    }

    @GetMapping(value = "/detail/{userMail}/{gif}")
    public ResponseEntity detail(@PathVariable("userMail") String userMail, @PathVariable("gif") String gif) throws IOException {
        QueryWrapper<ArtWork> query = new QueryWrapper<>();
        query.eq("email", userMail).eq("name", gif);
        var art = artwork.getOne(query);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(art);
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestParam String userMail, @RequestParam String name) {
        try {
            UpdateWrapper<ArtWork> update = new UpdateWrapper<>();
            update.eq("name", name).eq("email", userMail);
            artwork.remove(update);

            final String userDir = generateDir + userMail + '/';
            File toDel = new File(userDir + name);
            toDel.delete();

            return ResponseEntity
                .status(HttpStatus.OK)
                .body("deleted successfully");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("something wrong with deletion");
        }
    }

    private void saveArtwork(String userMail, String gifName, String by, int category) {
        ArtWork art = new ArtWork();
        UpdateWrapper<ArtWork> query = new UpdateWrapper<ArtWork>();
        query.eq("category", category);
        query.eq("email", userMail);
        query.eq("name", gifName);
        
        art.setCategory(category);
        art.setEmail(userMail);
        art.setName(gifName);
        art.setIllustrator(by);

        if (artwork.exists(query)) {
            artwork.remove(query);
        }
            
        artwork.save(art);
    }

    // Downloadable Content:
    @GetMapping("/drvv")
    public ResponseEntity drvv(@RequestParam String userMail, @RequestParam String path,
            @RequestParam String name, @RequestParam(defaultValue = "visitor") String by)
        throws IOException
    {
        petpet(userMail, path, name, by);
        String petpetName       = name + "-petpet.gif";
        String gifName          = name + "-drvv.gif";
        final String userDir = generateDir + userMail + '/';

        // final process:
        var drvvPortrait = ImageIO.read(getClass().getResourceAsStream(drvvRsc));
        var drvvBG = ImageIO.read(getClass().getResourceAsStream(drvvBgRsc));

        var vvDecoder = new GifDecoder();
        var vvEncoder = new AnimatedGifEncoder();
        vvDecoder.read(new FileInputStream(userDir + petpetName));
        log.info("directory = {}", userDir + petpetName);
        vvEncoder.start(userDir + gifName);
        vvEncoder.setDispose(3);
        vvEncoder.setRepeat(0);
        vvEncoder.setDelay(100);
        log.info("frameCount = {}", vvDecoder.getFrameCount());

        var firstFrame = vvDecoder.getFrame(0);
        final Integer vvW = drvvPortrait.getWidth();
        final Integer vvH = drvvPortrait.getHeight();
        final Integer W   = firstFrame.getWidth();
        final Integer H   = firstFrame.getHeight();
        final Integer outW = vvW + 2 * W;
        final Integer outH = vvH + H;
        Scalr.resize(drvvBG, Scalr.Mode.FIT_EXACT, outW, outH);

        for (int i = 0; i < vvDecoder.getFrameCount(); ++i) {
            BufferedImage canvas = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
            BufferedImage petpet = vvDecoder.getFrame(i);
            Graphics2D vv2d = (Graphics2D) canvas.getGraphics();
            BufferedImage flipped = Scalr.rotate(petpet, Scalr.Rotation.FLIP_HORZ);
            vv2d.drawImage(drvvBG, 0, 0, null);
            vv2d.drawImage(drvvPortrait, W, 0, null);
            vv2d.drawImage(flipped, 0, vvH, null);
            vv2d.drawImage(petpet, W + vvW, vvH, null);

            vvEncoder.addFrame(canvas);
        }

        vvEncoder.finish();

        saveArtwork(userMail, gifName, by, ImgenCategories.DRVV);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(gifName);
    }
}
