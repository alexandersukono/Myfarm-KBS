package rpp;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.WeightedAverage;
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
    Engine engine;
    InputVariable ketinggian, kelembaban, curah_hujan, ph;
    OutputVariable tanaman;
    RuleBlock ruleBlock;

    public MyFarm(){
        engine = new Engine();
        engine.setName("MyFarm-KBS");

        ketinggian = new InputVariable();
        ketinggian.setEnabled(true);
        ketinggian.setName("ketinggian");
        ketinggian.setRange(0, 3000);
        ketinggian.addTerm(new Triangle("rendah", 0, 400, 800));
        ketinggian.addTerm(new Triangle("sedang", 600, 1050, 1500));
        ketinggian.addTerm(new Triangle("tinggi", 1200, 2100, 3000));
        engine.addInputVariable(ketinggian);
        
        kelembaban = new InputVariable();
        kelembaban.setEnabled(true);
        kelembaban.setName("kelembaban");
        kelembaban.setRange(50, 100);
        kelembaban.addTerm(new Triangle("rendah", 50, 62.5, 75));
        kelembaban.addTerm(new Triangle("sedang", 70, 80, 90));
        kelembaban.addTerm(new Triangle("tinggi", 85, 92.5, 100));
        engine.addInputVariable(kelembaban);
        
        curah_hujan = new InputVariable();
        curah_hujan.setEnabled(true);
        curah_hujan.setName("curah_hujan");
        curah_hujan.setRange(0, 4000);
        curah_hujan.addTerm(new Triangle("bawah_normal", 0, 1000, 2000));
        curah_hujan.addTerm(new Triangle("normal", 1800, 2400, 3000));
        curah_hujan.addTerm(new Triangle("atas_normal", 2700, 3350, 4000));
        engine.addInputVariable(curah_hujan);
        
        ph = new InputVariable();
        ph.setEnabled(true);
        ph.setName("ph");
        ph.setRange(0, 14);
        ph.addTerm(new Triangle("asam_kuat", 0, 2.75, 5.5));
        ph.addTerm(new Triangle("asam_lemah", 5, 6, 7));
        ph.addTerm(new Triangle("netral", 6.5, 7, 7.5));
        ph.addTerm(new Triangle("basa_lemah", 7, 8, 9));
        ph.addTerm(new Triangle("basa_kuat", 8.5, 11.25, 14));
        engine.addInputVariable(ph);

        tanaman = new OutputVariable();
        tanaman.setEnabled(true);
        tanaman.setName("tanaman");
        tanaman.setDefaultValue(0);
        tanaman.setRange(0,listTanaman.length);
        tanaman.setDefuzzifier(new WeightedAverage("TakagiSugeno"));
        tanaman.fuzzyOutput().setAccumulation(null);
        tanaman.setLockPreviousOutputValue(true);
        tanaman.setLockOutputValueInRange(false);
        for(int i = 0; i < listTanaman.length; i++){
            tanaman.addTerm(new Constant(listTanaman[i],i));
        }
        engine.addOutputVariable(tanaman);
        
        ruleBlock = new RuleBlock();
        ruleBlock.setEnabled(true);
        ruleBlock.setName("");
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(null);
        ruleBlock.setActivation(null);
	ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is rendah and curah_hujan is bawah_normal then tanaman is tembakau with 0.63",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is rendah and curah_hujan is normal and ketinggian is rendah then tanaman is kelapa_sawit with 0.59",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is rendah and curah_hujan is normal and ketinggian is sedang then tanaman is karet with 0.84",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is rendah and curah_hujan is atas_normal then tanaman is kelapa_sawit with 0.55",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is bawah_normal and ketinggian is rendah then tanaman is kayu_manis with 0.49",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is bawah_normal and ketinggian is sedang then tanaman is kayu_manis with 0.62",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is bawah_normal and ketinggian is tinggi then tanaman is padi with 0.67",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is normal and ketinggian is rendah then tanaman is karet with 0.44",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is normal and ketinggian is sedang then tanaman is padi with 0.8",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is normal and ketinggian is tinggi then tanaman is kayu_manis with 0.53",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is sedang and curah_hujan is atas_normal then tanaman is tembakau with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is tinggi and ketinggian is rendah then tanaman is kelapa_sawit with 0.84",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is tinggi and ketinggian is sedang then tanaman is karet with 0.78",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_kuat and kelembaban is tinggi and ketinggian is tinggi then tanaman is padi with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is rendah and curah_hujan is bawah_normal then tanaman is tebu with 0.42",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is rendah and curah_hujan is normal then tanaman is sagu with 0.61",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is rendah and curah_hujan is atas_normal then tanaman is sagu with 0.51",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is sedang and curah_hujan is bawah_normal then tanaman is teh with 0.47",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is sedang and curah_hujan is normal then tanaman is teh with 0.24",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is sedang and curah_hujan is atas_normal then tanaman is karet with 0.43",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is tinggi and curah_hujan is bawah_normal then tanaman is teh with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is rendah and kelembaban is tinggi and curah_hujan is normal then tanaman is kopi_robusta with 0.5",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is bawah_normal and kelembaban is rendah then tanaman is kayu_manis with 0.71",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is bawah_normal and kelembaban is sedang then tanaman is kelapa with 0.31",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is bawah_normal and kelembaban is tinggi then tanaman is kopi_robusta with 0.6",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is normal and kelembaban is rendah then tanaman is sagu with 0.51",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is normal and kelembaban is sedang then tanaman is kopi_robusta with 0.29",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is normal and kelembaban is tinggi then tanaman is kentang with 0.36",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is atas_normal and kelembaban is rendah then tanaman is tembakau with 0.8",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is atas_normal and kelembaban is sedang then tanaman is tebu with 0.53",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is sedang and curah_hujan is atas_normal and kelembaban is tinggi then tanaman is karet with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is tinggi and kelembaban is rendah then tanaman is kopi_arabika with 0.6",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is tinggi and kelembaban is sedang and curah_hujan is bawah_normal then tanaman is kayu_manis with 0.38",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is tinggi and kelembaban is sedang and curah_hujan is normal then tanaman is padi with 0.47",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is tinggi and kelembaban is tinggi and curah_hujan is bawah_normal then tanaman is kentang with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is asam_lemah and ketinggian is tinggi and kelembaban is tinggi and curah_hujan is normal then tanaman is kopi_arabika with 0.87",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is rendah and curah_hujan is bawah_normal then tanaman is kapas with 0.52",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is rendah and curah_hujan is normal and kelembaban is rendah then tanaman is kelapa with 0.71",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is rendah and curah_hujan is normal and kelembaban is sedang then tanaman is kapas with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is rendah and curah_hujan is normal and kelembaban is tinggi then tanaman is kelapa with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is rendah and curah_hujan is atas_normal then tanaman is tebu with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is sedang and kelembaban is rendah then tanaman is tebu with 0.8",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is sedang and kelembaban is sedang then tanaman is tebu with 0.48",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is sedang and kelembaban is tinggi then tanaman is kentang with 0.62",engine));
        ruleBlock.addRule(Rule.parse("if ph is netral and ketinggian is tinggi then tanaman is kentang with 0.69",engine));
        ruleBlock.addRule(Rule.parse("if ph is basa_lemah and ketinggian is rendah then tanaman is kapas with 0.6",engine));
        ruleBlock.addRule(Rule.parse("if ph is basa_lemah and ketinggian is sedang then tanaman is kelapa with 1",engine));
        ruleBlock.addRule(Rule.parse("if ph is basa_kuat then tanaman is kapas with 1",engine));
        engine.addRuleBlock(ruleBlock);


        StringBuilder status = new StringBuilder();
        if (!engine.isReady(status)) {
            throw new RuntimeException("Engine not ready. "
                    + "The following errors were encountered:\n" + status.toString());
        }
    }
    
    public String classify(double _ketinggian, double _kelembaban, double _curah_hujan, double _ph){
        ketinggian.setInputValue(_ketinggian);
        kelembaban.setInputValue(_kelembaban);
        curah_hujan.setInputValue(_curah_hujan);
        ph.setInputValue(_ph);
            
        engine.process();
        return listTanaman[(int)tanaman.getOutputValue()];
    }
}