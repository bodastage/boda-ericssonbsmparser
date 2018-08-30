/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.bodastage.cm.ericssonbsmparser.EricssonBSMParser;
import java.io.File;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Emmanuel
 */
public final class EricssonBSMParserTests {
    private EricssonBSMParser parser;
    
    public void setUp() {
        parser = new EricssonBSMParser();
    }
    
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EricssonBSMParserTests.class );
    }
    
    public void testGetFileBasename(){
        String fileName = "/tmp/filename.csv";
        String expectedBaseName = "filename.csv";
        Assert.assertEquals(parser.getFileBasename(fileName),expectedBaseName);
    }
}
