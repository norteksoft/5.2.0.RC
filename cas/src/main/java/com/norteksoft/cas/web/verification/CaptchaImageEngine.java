package com.norteksoft.cas.web.verification;

import java.awt.Color;
import java.awt.Font;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.LineTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class CaptchaImageEngine extends ListImageCaptchaEngine {
	
	protected void buildInitialFactories() {
        /**
         * Set Captcha Word Length Limitation which should not over 6
         */
        Integer minAcceptedWordLength = new Integer(4);
        Integer maxAcceptedWordLength = new Integer(4);
        /**
         * Set up Captcha Image Size: Height and Width
         */
        Integer imageHeight = new Integer(24);
        Integer imageWidth = new Integer(74);
        /**
         * Set Captcha Font Size between 50 and 55
         */
        final Integer minFontSize = new Integer(18);
        final Integer maxFontSize = new Integer(22);
        /**
         * We just generate digit for captcha source char
         * Although you can use abcdefg......xyz
         */
        WordGenerator wordGenerator = (new RandomWordGenerator("01234567890123456789"));
        /**
         * cyt and unruledboy proved that backgroup not a factor of Security.
         * A captcha attacker won't affaid colorful backgroud, so we just use
         * white color, like google and hotmail.
         */
        //Color bgColor = new Color(255, 255, 255);
        BackgroundGenerator backgroundGenerator = new GradientBackgroundGenerator(
                imageWidth, imageHeight,  Color.orange, Color.white);
        /**
         * font is not helpful for security but it really increase difficultness for attacker
         */
        FontGenerator _fontGenerator = new FontGenerator() {
            public Font getFont() {
                return new Font("Arial", Font.ITALIC, 16);
            }
            public int getMinFontSize() {
                return minFontSize.intValue();
            }
            public int getMaxFontSize() {
                return maxFontSize.intValue();
            }
        };
        /**
         * Note that our captcha color is Blue
         */
        SingleColorGenerator scg = new SingleColorGenerator(Color.BLACK);
        
        /**
         * decorator is very useful pretend captcha attack.
         * we use two line text decorators.
         */
        LineTextDecorator line_decorator = new LineTextDecorator(new Integer(1), Color.blue);
        LineTextDecorator line_decorator2 = new LineTextDecorator(new Integer(1), Color.blue);
        TextDecorator[] textdecorators = new TextDecorator[2];

        textdecorators[0] = line_decorator;
        textdecorators[1] = line_decorator2;

        TextPaster _textPaster = new DecoratedRandomTextPaster(minAcceptedWordLength,
                maxAcceptedWordLength, scg, new TextDecorator[]{new BaffleTextDecorator(new Integer(0), Color.white)});

        /**
         * ok, generate the WordToImage Object for logon service to use.
         */
        WordToImage wordToImage = new ComposedWordToImage(
                _fontGenerator, backgroundGenerator, _textPaster);
        
        addFactory(new GimpyFactory(wordGenerator, wordToImage));
    }
}
