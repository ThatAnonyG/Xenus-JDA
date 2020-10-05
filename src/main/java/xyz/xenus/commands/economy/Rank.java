package xyz.xenus.commands.economy;

import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

// Todo - Complete the command
public class Rank extends Command {
    public Rank() {
        super("rank");
        setAliases(new String[]{"level"});
        setCategory(Categories.ECONOMY);
        setCd(5000);
        setDescription("Shows your level and XP in a graphical rank card.");
    }

    @Override
    public void run(CommandContext ctx) throws IOException {
        if (!ctx.getGuildModel().getEnabled().contains("xp")) return;

        Canvas canvas = new Canvas();
        BufferedImage bufferedImage = ImageIO.read(
                new URL(ctx.getEvent().getAuthor().getEffectiveAvatarUrl() + "?size=128")
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte[] data = outputStream.toByteArray();

        String name = ctx.getEvent().getAuthor().getName().length() > 20 ?
                ctx.getEvent().getAuthor().getName().substring(0, 17) + "..." :
                ctx.getEvent().getAuthor().getName();

        /**const canImage = new Canvas(400, 180)
         .setColor("#ffff00")
         .printRectangle(0, 0, 400, 180)
         .setColor("#23272a")
         .printRectangle(0, 0, 84, 180)
         .setColor("#23272a")
         .printRectangle(169, 26, 231, 46)
         .printRectangle(224, 108, 176, 46)
         .setShadowColor("rgba(0, 0, 0, 1)")
         .setShadowOffsetY(5)
         .setShadowBlur(10)
         .printCircle(84, 90, 62)
         .printCircularImage(image, 84, 90, 62)
         .save()
         .fill()
         .restore()
         .setTextAlign("center")
         .setTextFont("11pt Poppins")
         .setColor("lightgrey")
         .printText(`${name}'s Profile`, 285, 54)
         .printText(`Level ${message.member!.db!.eco.level}`, 86, 155)
         .setTextAlign("left")
         .printText(
         `XP:\xa0\xa0${message.member!.db!.eco.xp}/${
         message.member!.db!.eco.nxtLvl
         }`,
         241,
         136
         )
         .toBuffer("image/png");

         let rankCard = new MessageAttachment(
         canImage,
         `${message.author.username}.png`
         );
         message.channel.send(rankCard).catch();
         }**/
    }
}
