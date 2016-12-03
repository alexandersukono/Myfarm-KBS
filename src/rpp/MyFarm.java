package rpp;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.EinsteinProduct;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class MyFarm {
    public final static String[] listTanaman = {"unknown","kelapa_sawit","karet","tembakau","tebu","kelapa","kapas","kopi_arabika","kopi_robusta","kayu_manis","sagu","kentang","teh","padi"};

    public static void main(String[] args){
        Engine engine = new Engine();
        engine.setName("MyFarm-KBS");

        InputVariable ketinggian = new InputVariable();
        ketinggian.setName("ketinggian");
        ketinggian.setRange(0, 3000);
        ketinggian.addTerm(new Triangle("rendah", 0, 400, 800));
        ketinggian.addTerm(new Triangle("sedang", 600, 1050, 1500));
        ketinggian.addTerm(new Triangle("tinggi", 1200, 2100, 3000));
        engine.addInputVariable(ketinggian);
        
        InputVariable kelembaban = new InputVariable();
        kelembaban.setName("kelembaban");
        kelembaban.setRange(50, 100);
        kelembaban.addTerm(new Triangle("rendah", 50, 62.5, 75));
        kelembaban.addTerm(new Triangle("sedang", 70, 80, 90));
        kelembaban.addTerm(new Triangle("tinggi", 85, 92.5, 100));
        engine.addInputVariable(kelembaban);
        
        InputVariable curah_hujan = new InputVariable();
        curah_hujan.setName("curah_hujan");
        curah_hujan.setRange(0, 4000);
        curah_hujan.addTerm(new Triangle("bawah_normal", 0, 1000, 2000));
        curah_hujan.addTerm(new Triangle("normal", 1800, 2400, 3000));
        curah_hujan.addTerm(new Triangle("atas_normal", 2700, 3350, 4000));
        engine.addInputVariable(curah_hujan);
        
        InputVariable ph = new InputVariable();
        ph.setName("ph");
        ph.setRange(0, 4000);
        ph.addTerm(new Triangle("asam_kuat", 0, 2.75, 5.5));
        ph.addTerm(new Triangle("asam_lemah", 5, 6, 7));
        ph.addTerm(new Triangle("netral", 6.5, 7, 7.5));
        ph.addTerm(new Triangle("basa_lemah", 7, 8, 9));
        ph.addTerm(new Triangle("basa_kuat", 8.5, 11.25, 14));
        engine.addInputVariable(ph);

        OutputVariable tanaman = new OutputVariable();
        tanaman.setName("tanaman");
        tanaman.setDefaultValue(0);
        tanaman.setRange(0,listTanaman.length);
        for(int i = 0; i < listTanaman.length; i++){
            tanaman.addTerm(new Constant(listTanaman[i],i));
        }
        engine.addOutputVariable(tanaman);

        Rule r1 = new Rule();
        r1.setText("if ph is asam_kuat and kelembaban is rendah and curah_hujan is bawah_normal then tanaman is tembakau");
        r1.setWeight(0.63);
        
        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.addRule(r1);
        engine.addRuleBlock(ruleBlock);

        engine.configure("Minimum", "Maximum", "Minimum", "Maximum", "Centroid");

        StringBuilder status = new StringBuilder();
        if (!engine.isReady(status)) {
            throw new RuntimeException("Engine not ready. "
                    + "The following errors were encountered:\n" + status.toString());
        }
            
            ketinggian.setInputValue(800);
            kelembaban.setInputValue(60);
            curah_hujan.setInputValue(800);
            ph.setInputValue(5);
            
            engine.process();
//            FuzzyLite.logger().info(String.format(
//                    "ketinggian = %s m, kelembaban = %s %, curah hujan = %s mm/tahun, pH = %s -> tanaman = %s",
//                    Op.str(ketinggian.getInputValue()), Op.str(kelembaban.getInputValue()), Op.str(curah_hujan.getInputValue()), Op.str(ph.getInputValue()), Op.str(tanaman.getOutputValue())));
            System.out.println(ph.fuzzyInputValue());
            System.out.println(kelembaban.fuzzyInputValue());
            System.out.println(curah_hujan.fuzzyInputValue());
            System.out.println(tanaman.fuzzyOutputValue());
    }
}