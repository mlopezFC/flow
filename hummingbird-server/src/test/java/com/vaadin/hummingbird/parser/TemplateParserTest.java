package com.vaadin.hummingbird.parser;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.hummingbird.kernel.BoundElementTemplate;
import com.vaadin.hummingbird.kernel.Element;
import com.vaadin.hummingbird.kernel.ElementTemplate;
import com.vaadin.hummingbird.kernel.StateNode;

public class TemplateParserTest {
    @Test
    public void simpleParsing() {
        String templateString = "<input [value]=\"key\" type=\"text\">";
        StateNode node = StateNode.create();
        node.put("key", "inputvalue");

        ElementTemplate template = TemplateParser.parse(templateString);
        Element element = Element.getElement(template, node);

        Assert.assertEquals("input", element.getTag());
        Assert.assertEquals(0, element.getChildCount());

        Assert.assertEquals(2, element.getAttributeNames().size());

        Assert.assertEquals("inputvalue", element.getAttribute("value"));
        Assert.assertEquals("text", element.getAttribute("type"));
    }

    @Test
    public void staticChildTemplate() {
        String templateString = "<div class=\"parent\"><input [value]=\"key\"></div>";
        StateNode node = StateNode.create();
        node.put("key", "inputvalue");

        ElementTemplate template = TemplateParser.parse(templateString);
        Element element = Element.getElement(template, node);

        Assert.assertEquals("div", element.getTag());

        Assert.assertEquals("parent", element.getAttribute("class"));
        Assert.assertEquals(1, element.getChildCount());

        Element child = element.getChild(0);
        Assert.assertEquals("input", child.getTag());
        Assert.assertEquals(0, child.getChildCount());
        Assert.assertEquals("inputvalue", child.getAttribute("value"));
    }

    @Test
    public void staticInlineText() {
        String templateString = "<span>Hello</span>";
        StateNode node = StateNode.create();

        ElementTemplate template = TemplateParser.parse(templateString);
        Element element = Element.getElement(template, node);

        Assert.assertEquals(templateString, element.toString());
    }

    @Test
    public void dynamicText() {
        String templateString = "<span class=\"greeting\">{{greeting}}</span>";
        StateNode node = StateNode.create();
        node.put("greeting", "Hello, world");

        ElementTemplate template = TemplateParser.parse(templateString);
        Element element = Element.getElement(template, node);

        Assert.assertEquals("<span class=\"greeting\">Hello, world</span>",
                element.toString());
    }

    @Test
    public void forLoop() {
        String templateString = "<ul><li *ng-for='#todo of todos' [innertitle]='todo.title' [outertitle]='title'>{{todo.title}}</li></ul>";
        StateNode node = StateNode.create();
        node.put("title", "Outer title");
        List<Object> todos = node.getMultiValued("todos");
        IntStream.range(0, 3).forEach(i -> {
            StateNode child = StateNode.create();
            child.put("title", "Todo " + i);
            todos.add(child);
        });

        ElementTemplate template = TemplateParser.parse(templateString);
        Element element = Element.getElement(template, node);

        Assert.assertEquals("ul", element.getTag());
        Assert.assertEquals(3, element.getChildCount());
        for (int i = 0; i < 3; i++) {
            Element li = element.getChild(i);
            Assert.assertEquals(1, li.getChildCount());
            Assert.assertEquals(2, li.getAttributeNames().size());
            Assert.assertEquals("Outer title", li.getAttribute("outertitle"));
            Assert.assertEquals("Todo " + i, li.getAttribute("innertitle"));
            Assert.assertEquals("Todo " + i, li.getChild(0).getOuterHTML());
        }
    }

    @Test
    public void testScriptTag() {
        String templateString = "<div><script type='text/javascript'>window.alert('hello');</script></div>";
        ElementTemplate template = TemplateParser.parse(templateString);

        Element element = Element.getElement(template, StateNode.create());
        Assert.assertEquals(1, element.getChildCount());

        Element script = element.getChild(0);

        Assert.assertEquals("script", script.getTag());
        Assert.assertEquals("text/javascript", script.getAttribute("type"));
        Assert.assertEquals("window.alert('hello');", script.getTextContent());
    }

    @Test
    public void acceptRootComments() {
        String templateString = "<!-- pre --><div></div><!-- post -->";
        BoundElementTemplate template = (BoundElementTemplate) TemplateParser
                .parse(templateString);

        Assert.assertEquals("div", template.getTag());
    }

    @Test(expected = TemplateException.class)
    public void failForMultipleRoots() {
        String templateString = "<div></div><div></div>";
        TemplateParser.parse(templateString);
    }

    @Test(expected = TemplateException.class)
    public void failForEmptyTemplate() {
        String templateString = "";
        TemplateParser.parse(templateString);
    }

    @Test(expected = TemplateException.class)
    public void failForCommentOnlyTemplate() {
        String templateString = "<!-- comment -->";
        TemplateParser.parse(templateString);
    }

}
