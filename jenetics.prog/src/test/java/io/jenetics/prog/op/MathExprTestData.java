/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.prog.op;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.rint;
import static java.lang.Math.sin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.processing.Generated;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Generated("manually")
public class MathExprTestData {

	@FunctionalInterface
	public interface Fun3 {
		double apply(final double x, final double y, final double z);
	}

	public static final List<String> EXPRESSIONS = CSV.read(
		MathExprTestData.class
			.getResourceAsStream("/io/jenetics/prog/op/expressions.csv")
	);

	public static final List<List<String>> EXPRESSIONS_TOKENS =
		CSV.read(MathExprTestData.class
				.getResourceAsStream("/io/jenetics/prog/op/expressions_tokens.csv"))
			.stream()
			.map(line -> Arrays.asList(line.split(Pattern.quote("|"))))
			.toList();

	public static final List<Fun3> FUNCTIONS = List.of(
		MathExprTestData::fun_0,
		MathExprTestData::fun_1,
		MathExprTestData::fun_2,
		MathExprTestData::fun_3,
		MathExprTestData::fun_4,
		MathExprTestData::fun_5,
		MathExprTestData::fun_6,
		MathExprTestData::fun_7,
		MathExprTestData::fun_8,
		MathExprTestData::fun_9,
		MathExprTestData::fun_10,
		MathExprTestData::fun_11,
		MathExprTestData::fun_12,
		MathExprTestData::fun_13,
		MathExprTestData::fun_14,
		MathExprTestData::fun_15,
		MathExprTestData::fun_16,
		MathExprTestData::fun_17,
		MathExprTestData::fun_18,
		MathExprTestData::fun_19,
		MathExprTestData::fun_20,
		MathExprTestData::fun_21,
		MathExprTestData::fun_22,
		MathExprTestData::fun_23,
		MathExprTestData::fun_24,
		MathExprTestData::fun_25,
		MathExprTestData::fun_26,
		MathExprTestData::fun_27,
		MathExprTestData::fun_28,
		MathExprTestData::fun_29,
		MathExprTestData::fun_30,
		MathExprTestData::fun_31,
		MathExprTestData::fun_32,
		MathExprTestData::fun_33,
		MathExprTestData::fun_34,
		MathExprTestData::fun_35,
		MathExprTestData::fun_36,
		MathExprTestData::fun_37,
		MathExprTestData::fun_38,
		MathExprTestData::fun_39,
		MathExprTestData::fun_40,
		MathExprTestData::fun_41,
		MathExprTestData::fun_42,
		MathExprTestData::fun_43,
		MathExprTestData::fun_44,
		MathExprTestData::fun_45,
		MathExprTestData::fun_46,
		MathExprTestData::fun_47,
		MathExprTestData::fun_48,
		MathExprTestData::fun_49,
		MathExprTestData::fun_50,
		MathExprTestData::fun_51,
		MathExprTestData::fun_52,
		MathExprTestData::fun_53,
		MathExprTestData::fun_54,
		MathExprTestData::fun_55,
		MathExprTestData::fun_56,
		MathExprTestData::fun_57,
		MathExprTestData::fun_58,
		MathExprTestData::fun_59,
		MathExprTestData::fun_60,
		MathExprTestData::fun_61,
		MathExprTestData::fun_62,
		MathExprTestData::fun_63,
		MathExprTestData::fun_64,
		MathExprTestData::fun_65,
		MathExprTestData::fun_66,
		MathExprTestData::fun_67,
		MathExprTestData::fun_68,
		MathExprTestData::fun_69,
		MathExprTestData::fun_70,
		MathExprTestData::fun_71,
		MathExprTestData::fun_72,
		MathExprTestData::fun_73,
		MathExprTestData::fun_74,
		MathExprTestData::fun_75,
		MathExprTestData::fun_76,
		MathExprTestData::fun_77,
		MathExprTestData::fun_78,
		MathExprTestData::fun_79,
		MathExprTestData::fun_80,
		MathExprTestData::fun_81,
		MathExprTestData::fun_82,
		MathExprTestData::fun_83,
		MathExprTestData::fun_84,
		MathExprTestData::fun_85,
		MathExprTestData::fun_86,
		MathExprTestData::fun_87,
		MathExprTestData::fun_88,
		MathExprTestData::fun_89,
		MathExprTestData::fun_90,
		MathExprTestData::fun_91,
		MathExprTestData::fun_92,
		MathExprTestData::fun_93,
		MathExprTestData::fun_94,
		MathExprTestData::fun_95,
		MathExprTestData::fun_96,
		MathExprTestData::fun_97,
		MathExprTestData::fun_98,
		MathExprTestData::fun_99,
		MathExprTestData::fun_100,
		MathExprTestData::fun_101,
		MathExprTestData::fun_102,
		MathExprTestData::fun_103,
		MathExprTestData::fun_104,
		MathExprTestData::fun_105,
		MathExprTestData::fun_106,
		MathExprTestData::fun_107,
		MathExprTestData::fun_108,
		MathExprTestData::fun_109,
		MathExprTestData::fun_110,
		MathExprTestData::fun_111,
		MathExprTestData::fun_112,
		MathExprTestData::fun_113,
		MathExprTestData::fun_114,
		MathExprTestData::fun_115,
		MathExprTestData::fun_116,
		MathExprTestData::fun_117,
		MathExprTestData::fun_118,
		MathExprTestData::fun_119,
		MathExprTestData::fun_120,
		MathExprTestData::fun_121,
		MathExprTestData::fun_122,
		MathExprTestData::fun_123,
		MathExprTestData::fun_124,
		MathExprTestData::fun_125,
		MathExprTestData::fun_126,
		MathExprTestData::fun_127,
		MathExprTestData::fun_128,
		MathExprTestData::fun_129,
		MathExprTestData::fun_130,
		MathExprTestData::fun_131,
		MathExprTestData::fun_132,
		MathExprTestData::fun_133,
		MathExprTestData::fun_134,
		MathExprTestData::fun_135,
		MathExprTestData::fun_136,
		MathExprTestData::fun_137,
		MathExprTestData::fun_138,
		MathExprTestData::fun_139,
		MathExprTestData::fun_140,
		MathExprTestData::fun_141,
		MathExprTestData::fun_142,
		MathExprTestData::fun_143,
		MathExprTestData::fun_144,
		MathExprTestData::fun_145,
		MathExprTestData::fun_146,
		MathExprTestData::fun_147,
		MathExprTestData::fun_148,
		MathExprTestData::fun_149
	);

	private static double fun_0(final double x, final double y, final double z) {
		return min(sin(z+max((23.43e-03+min(y%pow(z,6.345345),1.4e3)-(abs(3.123312)%(min(pow(6.345345*1.4e3,(x*y)*pow(23.43e-03,1.4e3)),(23.43e-03-y))+(x%min(sin((3.123312*min(23.43e-03,z-3.123312-min(z,x)))),3.123312))))),(rint(3.123312)-max((x-y),z/abs(max(max(cos(1.4e3),23.43e-03),y-sin(x)))))))%x,1.4e3);
	}

	private static double fun_1(final double x, final double y, final double z) {
		return abs(1.4e3)%pow((sin(max(cos(cos(23.43e-03)),sin(x)))-1.4e3%min(23.43e-03,z)),3.123312-pow(1.4e3,abs((sin(((y*z)*rint(max(x,z-(y*min(1.4e3,(3.123312-z-23.43e-03)/y+pow(pow(max(z,6.345345),(cos(max(sin(23.43e-03%min(z,z)),1.4e3))+(min(3.123312,(max(23.43e-03,min(x,rint(y)))*min(min(abs(6.345345),sin(3.123312)),6.345345)))%(6.345345-x)))%cos(z)),(23.43e-03+1.4e3*x%1.4e3))))))))*(sin(pow(((6.345345%3.123312)-(z*3.123312)),x))+z)))));
	}

	private static double fun_2(final double x, final double y, final double z) {
		return (max(6.345345*3.123312,pow(sin(sin(min(y,6.345345*x/abs((x/pow(((min(z,6.345345)*y)/(23.43e-03-pow(1.4e3,23.43e-03%pow(pow(cos(x),3.123312),pow(1.4e3,cos(cos(3.123312))))))),((max(sin(1.4e3)+rint(y-6.345345)-pow(sin(z),z),pow((23.43e-03-max(rint(6.345345),sin(x))-cos(y)%rint(y)),max(rint(y),cos(max(rint(rint(((abs(y)+z)/rint(x)))),1.4e3))*23.43e-03)))+6.345345/y)-6.345345-rint(3.123312)-sin(x)))))-z/x))),x))%abs(min(min(3.123312,abs((1.4e3*y))),1.4e3)));
	}

	private static double fun_3(final double x, final double y, final double z) {
		return (x/max(x,min(x%(6.345345-max(3.123312,((3.123312+y)*6.345345)))/max(rint(z*max(23.43e-03,y))*cos(23.43e-03),min((y+abs(x)),y)),y*1.4e3+(3.123312%rint(cos(max(1.4e3,sin(x))))))/sin(y))%max(x-y,pow(cos(abs(x)),abs(z)+(rint(min((y/1.4e3%(x%z)),cos(y)))-min(sin(x)/1.4e3,(3.123312%sin((1.4e3*y))))))));
	}

	private static double fun_4(final double x, final double y, final double z) {
		return (rint(abs(max((pow(abs(abs(abs(z))),23.43e-03-6.345345%x)/23.43e-03),23.43e-03+3.123312)))/max(max((1.4e3%x),sin(6.345345)),pow(x,min(23.43e-03-abs(abs(x)/(pow((pow(x-23.43e-03,y)*pow(1.4e3,pow(cos(6.345345)/x,y)+abs(sin(z)))),((max(y,1.4e3)+sin((x-3.123312)))/sin(3.123312)))*6.345345%3.123312)),x))-cos(x)));
	}

	private static double fun_5(final double x, final double y, final double z) {
		return sin(rint(max(6.345345,abs(((min(6.345345/(min(max(y,6.345345),x)+(y%sin(6.345345)))+cos(3.123312),abs(x%pow(rint(pow(6.345345,x)),cos(x))))/(23.43e-03*(rint(z)%1.4e3/sin(6.345345))))*pow(z,(max(x,sin(rint(x))/(6.345345/(sin((y-pow(3.123312-x,(max(1.4e3,3.123312)+x))/(max(((abs(z)*abs(min(rint(abs((((1.4e3/z-23.43e-03)-x%z-(y/max(pow(rint((6.345345%6.345345)),(x%(cos(rint(y))*abs(x)))),x)))%max(z,z)*((6.345345%23.43e-03)*(z-y))))),y))-y%cos(1.4e3))/6.345345+3.123312-6.345345*6.345345)-1.4e3,1.4e3)+3.123312)-abs(23.43e-03)))+abs(x))))%23.43e-03)))))));
	}

	private static double fun_6(final double x, final double y, final double z) {
		return sin(((pow(min(min((z/(6.345345-max(y,sin(1.4e3)))),max(((x-6.345345-1.4e3*6.345345*z)%max(x,(3.123312*(((6.345345/abs((abs(x*sin(sin(min(23.43e-03,abs(3.123312-23.43e-03)))+3.123312))-6.345345)))*23.43e-03)%3.123312/y)))),rint(y))),6.345345-sin(pow(y,abs(1.4e3)))),6.345345+z)/rint(cos(x))+z)*sin(sin(3.123312))%3.123312)-3.123312);
	}

	private static double fun_7(final double x, final double y, final double z) {
		return sin((y-min(23.43e-03,pow((3.123312-6.345345)*(3.123312/abs(6.345345)),rint(y/sin(1.4e3*cos(min(z,6.345345*max(3.123312/abs(rint(y)),z)/min(z,rint(rint(max((cos(6.345345)%6.345345),3.123312)))))))))/pow((23.43e-03-1.4e3)-x,3.123312))-3.123312))/(z/23.43e-03);
	}

	private static double fun_8(final double x, final double y, final double z) {
		return min((((max(min(y,1.4e3),pow(y,pow(x,cos(cos(x)))))-min(pow(6.345345,rint((cos(y)%cos(1.4e3)))),23.43e-03)/(sin(sin(cos(((3.123312*pow(1.4e3,1.4e3))/3.123312))))/y))%z)*1.4e3*sin(1.4e3)%abs(sin(23.43e-03))),sin(((sin(y)/rint((x/rint(3.123312/(1.4e3+(y*pow(1.4e3,x)))))))/23.43e-03)));
	}

	private static double fun_9(final double x, final double y, final double z) {
		return max((1.4e3/3.123312-sin(z)-sin((6.345345*6.345345))*3.123312%x-cos(sin(23.43e-03)))+(23.43e-03*6.345345)/y,1.4e3*3.123312*min((x*z%x)/z,pow(max((x%23.43e-03),min(x,x))/cos((y*1.4e3)),1.4e3/3.123312))+(z+sin((rint(cos(3.123312))/pow(23.43e-03,y)*(1.4e3-z)))/23.43e-03%x));
	}

	private static double fun_10(final double x, final double y, final double z) {
		return (23.43e-03/max(1.4e3,(cos(1.4e3)/sin(max(min((cos(x)-(23.43e-03-sin(max(cos(min(pow(z,(1.4e3-z)),sin(min((y*z),pow(1.4e3,abs(23.43e-03))))*x+rint(x))),(min(3.123312,3.123312)-3.123312))/min(3.123312,cos(23.43e-03))))),sin(y)),max(cos(z),z))%pow(min(23.43e-03,23.43e-03)/y,y)))));
	}

	private static double fun_11(final double x, final double y, final double z) {
		return pow(z,rint((sin(6.345345%abs(6.345345))+max(abs(1.4e3)*max((sin(min(6.345345+max((min(pow(x,23.43e-03-z),max(3.123312,(x+min(z,y))))%max(rint(23.43e-03),sin(max(rint(max(min(6.345345,6.345345)*(z/pow((x*1.4e3)-6.345345,z)),(sin(23.43e-03)/rint(rint(1.4e3))))),23.43e-03)))),1.4e3),max(pow(z,23.43e-03),rint((cos(x)%6.345345-6.345345)))))*x),(rint(6.345345)%x)/rint((1.4e3%max(max(3.123312,rint(3.123312)),min(6.345345,z)))+23.43e-03)),y))));
	}

	private static double fun_12(final double x, final double y, final double z) {
		return rint(abs((3.123312*(max(pow(x,max(23.43e-03,z))/z*y,(sin(rint((23.43e-03-abs(min(x,6.345345))))/(pow(3.123312,23.43e-03%abs(sin(sin(z))))/max(rint(23.43e-03),abs(y))))*z))+(max(z,abs(x))*min(min(sin(cos((rint(1.4e3)+(max(max(z,6.345345),x)+3.123312)))),x),6.345345))))%min(y,x)));
	}

	private static double fun_13(final double x, final double y, final double z) {
		return max(z,3.123312-(abs(23.43e-03)+23.43e-03)+((max(1.4e3,(y/z))/(sin(23.43e-03)%y)*pow(min((((cos(z)-max(6.345345,abs(1.4e3)))*sin((x%(6.345345%rint((z*6.345345-(x%1.4e3)*x+23.43e-03%z))))))+min(z,x))%rint(23.43e-03),3.123312),(1.4e3*(cos(23.43e-03)+z%x))))%rint(sin(1.4e3/3.123312))));
	}

	private static double fun_14(final double x, final double y, final double z) {
		return abs(min(cos(((y+pow(sin(y),(y*rint(rint(1.4e3)))/y))-23.43e-03)),(pow(min(x,3.123312-3.123312)/(z/23.43e-03)%(z%rint(y)%(sin(y)*min((cos(min(3.123312,x))+min(3.123312,3.123312)),abs(max(z,(x*y))%z))-z))*1.4e3,23.43e-03)+max(y,(((z/(z-6.345345))+23.43e-03)%x+1.4e3*max(max(6.345345,23.43e-03),z)%3.123312)))-cos(x)%cos((x*y)*rint((y*x))))%1.4e3);
	}

	private static double fun_15(final double x, final double y, final double z) {
		return cos(min(sin(6.345345),(x+1.4e3))-(23.43e-03+23.43e-03)-((1.4e3-(((max(sin((23.43e-03+x)),pow(23.43e-03%3.123312/x,rint(cos(y))/1.4e3/z))-6.345345*z+6.345345-3.123312)*z-(z%max((x/y),rint(pow(((cos(cos(z))*y)*pow(cos(y),x)),23.43e-03))))-abs(6.345345)-(y/abs(6.345345)))+y))*x-pow(y/min((cos(x)*((x*x+x*23.43e-03)-min((23.43e-03*cos(6.345345)),x))+y),x),1.4e3)));
	}

	private static double fun_16(final double x, final double y, final double z) {
		return min(rint(abs((x%abs(z)))*abs(((sin(max(rint(((z-6.345345)-pow(pow(x+z,(sin(min(abs(y)/rint(rint((cos(pow(rint(min(rint(3.123312),x*pow(x,(min(y,y)-rint(cos(z)+1.4e3))))),z))+6.345345)))%y,y))%pow(cos((sin(abs(min(23.43e-03,rint(sin(x)))))%x)),z))),min(3.123312,cos(y))))),x))%abs(3.123312))*sin(1.4e3/6.345345)))),rint((6.345345*z)));
	}

	private static double fun_17(final double x, final double y, final double z) {
		return ((x-(z-y+abs(max((x/abs(cos(y)*pow(6.345345,rint(z)))),(rint(max((abs((3.123312+(pow(abs(x%max(min((cos(23.43e-03)*23.43e-03),z),(3.123312/abs((min((y%(x%z)),6.345345)*y))+23.43e-03))),(sin(6.345345)*pow(pow(1.4e3,cos(cos(6.345345))),3.123312)*y))/sin((rint(abs(pow(z,y)))*y/rint(sin(1.4e3)+1.4e3))))))%6.345345),z))*(abs(rint(z))%3.123312))))))+y);
	}

	private static double fun_18(final double x, final double y, final double z) {
		return max(((sin(pow(max(x,3.123312),rint(rint(y)))+3.123312)%x)+(6.345345/((6.345345+sin(cos(abs(sin(x)))))%1.4e3)*min(23.43e-03,sin(min(z,(3.123312+y*3.123312%(cos(cos((sin(rint(min(max(23.43e-03,z),y)))*23.43e-03))*abs(6.345345))%y)*(cos(abs(z))%23.43e-03*pow(23.43e-03,6.345345%cos(3.123312))+(abs(cos(z))-(rint(abs(cos(23.43e-03)))/pow(1.4e3,y)%cos(y)))))))))/(6.345345%rint(abs(6.345345-(cos(max(x,y+z))%(6.345345+(rint(1.4e3)+abs(6.345345)))*3.123312*rint((rint(pow(rint((x-x*min((abs(z)*abs(23.43e-03)),3.123312)))/y+pow(y,z),sin(1.4e3)))*cos(3.123312)-((x*abs(x))-z)))/sin(y)))))),(((sin(min(sin(x),3.123312))/pow(((x-y)-6.345345)*pow((3.123312*3.123312*rint(z)),y),3.123312))-6.345345*abs(y))/1.4e3/y));
	}

	private static double fun_19(final double x, final double y, final double z) {
		return (1.4e3+pow(max(3.123312,max(cos(z),6.345345)+rint(pow((min(pow(x/23.43e-03,1.4e3),23.43e-03+1.4e3)%abs((rint(1.4e3)/rint(((sin(x)-23.43e-03)%rint(3.123312)))/(3.123312/pow(pow(y,z+1.4e3),6.345345)*(x+pow(cos(6.345345),min(z,(min(cos(sin(1.4e3)),6.345345)+6.345345)))))))-y%x),y))),pow((6.345345%x)/z,sin(max(6.345345,y)-((3.123312-cos(((3.123312-(y/rint((6.345345%6.345345))-y+x))/z)%cos(cos(23.43e-03))))-3.123312+abs(y))))));
	}

	private static double fun_20(final double x, final double y, final double z) {
		return 23.43e-03+min(x,min(max((y*pow(3.123312,y))+y%1.4e3,sin(6.345345)),abs(23.43e-03)-y-abs(abs(23.43e-03))-rint(6.345345)+max(1.4e3,abs(cos(cos((abs((6.345345-rint(6.345345)))-min((x*(max(rint(1.4e3)%sin(1.4e3)+rint(min(23.43e-03,abs(y)))*3.123312,y)%z)-z),y))))))));
	}

	private static double fun_21(final double x, final double y, final double z) {
		return (x*23.43e-03-pow(rint(z),rint(min((sin(max(3.123312,min(min((3.123312%cos(cos(max(23.43e-03,abs(6.345345))))*min(rint(23.43e-03),pow(rint(x),sin(1.4e3)-y)%rint(x))%z),23.43e-03),(23.43e-03%pow(x,1.4e3)-23.43e-03))+z))*max((x-x-x+max(x,y)),y)),(x*23.43e-03*x))*y)));
	}

	private static double fun_22(final double x, final double y, final double z) {
		return sin(abs((y/max(rint(pow(((3.123312+y)%6.345345-6.345345),6.345345*z)+3.123312+sin(x)),(3.123312*rint(pow(rint(min(23.43e-03,min(x-6.345345,(x+y))))/pow(y,pow(z,x/cos(rint(y))))-(y+(23.43e-03*cos(23.43e-03))),y%cos(cos(((rint(6.345345)*(cos((pow(z,rint(pow(z*z%pow(abs(1.4e3),sin(6.345345)),(cos(23.43e-03)/6.345345))))-23.43e-03-(1.4e3%sin(23.43e-03)+y)*23.43e-03))/6.345345/rint(cos(z)%x)))*23.43e-03))))))))));
	}

	private static double fun_23(final double x, final double y, final double z) {
		return max(23.43e-03-(6.345345-min(max(sin(rint(3.123312/x/z%((cos((cos(x)%1.4e3*pow(6.345345,6.345345)/3.123312%pow(sin(z),sin(sin(min(x,1.4e3))))))-(1.4e3*(max(z+rint((1.4e3%1.4e3))*(1.4e3+(1.4e3*rint(x))),(cos(3.123312)%(3.123312%z)))-x-z))%x)+sin(6.345345)))),sin(x)),1.4e3)-abs(6.345345)*6.345345),x)*y;
	}

	private static double fun_24(final double x, final double y, final double z) {
		return max((x-cos((3.123312/min(z,cos(sin(abs(((3.123312-(3.123312-max(y,6.345345)*min(sin(z),min(x,max(1.4e3%rint(1.4e3),sin(pow(z,rint((((z%(y/max((y-min((1.4e3/sin(23.43e-03)),rint(23.43e-03)))/y,x)+y%23.43e-03))/y)%(x%y))))))))))/23.43e-03)))))))),1.4e3+cos(6.345345));
	}

	private static double fun_25(final double x, final double y, final double z) {
		return abs((cos(pow(pow(pow((3.123312/z),1.4e3),z),cos(rint(1.4e3))))%pow(pow(sin((min(6.345345,(z/sin(x)))*cos(3.123312/6.345345%23.43e-03))*cos(rint(y/23.43e-03*cos(1.4e3)/23.43e-03))),pow(min(x,6.345345),abs(1.4e3)))/max(max(1.4e3+sin(y)%23.43e-03,y),3.123312),max(3.123312,6.345345))));
	}

	private static double fun_26(final double x, final double y, final double z) {
		return ((3.123312+z/1.4e3)*(y*23.43e-03+min(pow((sin((y-abs(abs(1.4e3)%3.123312)+min(x,max((z-y),pow(6.345345,rint(y))%x)/x)%y))-z)-23.43e-03,(6.345345*abs(min((z*z),y)))*cos(z)),pow((23.43e-03/pow(min(rint(max(y,3.123312))-y%1.4e3,abs(3.123312)),6.345345)),max(1.4e3,z)))))*max(y,y);
	}

	private static double fun_27(final double x, final double y, final double z) {
		return cos(max(x,pow(pow(cos((max(pow(z,3.123312),6.345345)*min((z*y),x))),(x+6.345345)),rint(((z%cos(((z/(min(y,(x*y))+6.345345)+rint(6.345345))-y)))%abs(pow(y,6.345345))+pow(sin(rint(3.123312)),1.4e3))*cos(6.345345)))*abs((x+cos(sin(abs(sin(rint((23.43e-03-sin(y%1.4e3)))/6.345345+3.123312-3.123312*rint(y+6.345345)%x)/abs(z)))))))-cos(rint(1.4e3))*max(3.123312,z));
	}

	private static double fun_28(final double x, final double y, final double z) {
		return (pow(6.345345,abs(x))%3.123312-y%y+(min(23.43e-03,1.4e3+max((min(pow(1.4e3,abs((z/1.4e3))),cos(3.123312))/max(x,1.4e3)),pow((rint(23.43e-03-3.123312)%x),sin(abs(cos(z))))))%y)-(cos(max(pow(23.43e-03%cos(z),sin(y)),cos(y)))/min(3.123312,z)/abs(abs(z))));
	}

	private static double fun_29(final double x, final double y, final double z) {
		return (pow(min(z,x),1.4e3)/23.43e-03+max(pow(y,pow(pow((rint(z)%sin(23.43e-03)),min(6.345345,(23.43e-03+3.123312-sin(rint(pow(x%sin(23.43e-03),y)))))),pow(rint((23.43e-03%z)),x)+(max(y,(max(6.345345,23.43e-03)-(sin(y+pow(6.345345,z))-x)))%rint(min(abs(sin(rint(pow(((x/z%3.123312)+abs(max(1.4e3,max(pow(23.43e-03,(1.4e3/y%min(min(y,(y%rint((cos(sin(rint((abs(y)*cos(3.123312)))))+(23.43e-03-min(y,y%y%max(pow(z,1.4e3),6.345345))))))),abs(sin((x/6.345345)))))),x)))),3.123312)))),x))/x))),3.123312));
	}

	private static double fun_30(final double x, final double y, final double z) {
		return max(min(max(max(abs(min(1.4e3,y)),min(rint(cos(((y-1.4e3)+z))),pow(y,x-6.345345/6.345345))+pow((sin(y%y)/cos(x)),sin((sin(rint((6.345345-y)))/23.43e-03))))*sin(x)%(rint(23.43e-03-x)+sin((cos(y*6.345345)-((min(3.123312,23.43e-03)+rint(sin(rint((rint(x)+rint(23.43e-03)))))/x)-6.345345)/6.345345+1.4e3)-(3.123312*x)))/1.4e3-y+y-z,z),(y*(z+y*max(((6.345345*z)-pow((z%z*1.4e3)-3.123312,z)),y))%3.123312-cos(x)+sin(rint(pow((23.43e-03*cos(abs(max(min(1.4e3,23.43e-03),z))-min(pow((sin(y*cos(6.345345))/y*pow(y%6.345345-((rint(3.123312)-z)+(min((x/y*z),3.123312)/6.345345)),3.123312)),sin((sin((rint(pow(abs(rint(z)),pow(y,rint(y)+x)))-y))*x))),(3.123312*1.4e3)))-z/y),1.4e3)+abs(3.123312)))*3.123312))/sin(23.43e-03),6.345345);
	}

	private static double fun_31(final double x, final double y, final double z) {
		return 1.4e3%23.43e-03/sin(cos(pow((max((1.4e3/rint((abs(1.4e3)/3.123312))),y)*sin(max(pow(max(1.4e3,6.345345)%x-23.43e-03,(cos(x)+abs(min(rint(y)%sin((1.4e3+pow(pow(sin(23.43e-03),(cos(y+x)-cos(min(sin(cos(((y+x)+3.123312)+max(6.345345,(cos(abs(z))+x)))%(z/3.123312)*(z/(cos(sin(3.123312))*x))),pow(abs(abs(6.345345))/(x+sin(y))-sin(6.345345)*abs(y),x))))),3.123312))),23.43e-03))*z/y)),min(6.345345,x))))%(sin(y)*x)/23.43e-03-23.43e-03,6.345345)));
	}

	private static double fun_32(final double x, final double y, final double z) {
		return (rint(y)%23.43e-03%6.345345*(sin(6.345345)*abs(1.4e3))+z+max(x,cos(sin(rint(x)))+max(23.43e-03,6.345345))-z/23.43e-03/pow((rint(max((pow(min((z*rint(abs(23.43e-03-y*x))),max(3.123312,min((abs(6.345345+23.43e-03)%y),cos(rint(6.345345/(z%(x+6.345345)/abs(rint(cos(y))))))))),y)*z),pow(y,23.43e-03)-((((min(z,(x/min(x,z+23.43e-03)%rint(max(x,6.345345))))%3.123312)+z)*cos(abs(rint(cos(6.345345))))-z)*z+max(min(23.43e-03,pow(rint(sin(max(min(max(min(min(6.345345,z),23.43e-03)%abs((rint(cos((rint(z)%sin(y))-1.4e3))+sin(y))),6.345345)/y/z,x*(6.345345-3.123312)),1.4e3))*23.43e-03),1.4e3%(x+x*6.345345+23.43e-03))%(23.43e-03-rint(6.345345)+23.43e-03))%abs(sin(pow((sin(cos(z))*(z%rint(min(3.123312,3.123312)))),x)%3.123312))+sin(min(z,3.123312)),z))))-(x+23.43e-03)),pow(y,pow(pow(3.123312,sin(min(y,y)))-23.43e-03,max(z,y))))-23.43e-03*3.123312);
	}

	private static double fun_33(final double x, final double y, final double z) {
		return y-sin(abs(((min(pow(1.4e3,(3.123312-3.123312)),max((cos(pow(pow(3.123312,23.43e-03/23.43e-03+sin(y))%(cos((max(sin((z+((1.4e3/6.345345)*abs(pow(x,pow((z*min(y,y))/(rint(6.345345)-cos(y)/min(x,6.345345)),pow(pow(sin(max(cos(abs(rint(x))),max(sin(3.123312)*1.4e3,cos(23.43e-03)))),z),abs(max(3.123312-(x-min(abs(min(x,pow(1.4e3*(x%(6.345345+1.4e3)),23.43e-03))),23.43e-03)%y)*x,3.123312))))))))),cos(pow(3.123312,3.123312)))+(max((rint(rint(z))+x%1.4e3),3.123312)-(sin(23.43e-03)%z))))+min(sin(abs(max(6.345345,y/cos(x)))),y)),z))*(y%x))+1.4e3,sin(min(1.4e3,pow(1.4e3,abs(sin(x-rint(x))))))/(y-z)))+rint(23.43e-03)+y)+z)));
	}

	private static double fun_34(final double x, final double y, final double z) {
		return abs((cos(x)%(x/23.43e-03))/min((sin(sin(23.43e-03))%1.4e3),max(min(rint(z),3.123312)*max(6.345345,min((max(z,3.123312)/min((x-max(y/(cos(y)%(max(x,(6.345345-z)-abs((rint(rint(1.4e3))-23.43e-03))-1.4e3)%(z%y%3.123312))),abs(6.345345/x))+(z/cos(z-x))),1.4e3)-rint((sin(z)%23.43e-03))),x)*sin(rint(sin(z)))*23.43e-03),3.123312)));
	}

	private static double fun_35(final double x, final double y, final double z) {
		return sin(sin(max(x,((pow(6.345345,abs(1.4e3))+abs(pow(pow(1.4e3+min(x,z),y),rint((z*sin(pow(min(cos(x),cos(6.345345)),(pow(max(6.345345,z),1.4e3)*(abs(23.43e-03)+6.345345+1.4e3)/pow(abs(abs(z%1.4e3*y))%abs(x%sin(abs(max(3.123312,23.43e-03)))),pow((23.43e-03-sin(3.123312)),y))))/y)%max((y+pow(y,z)),6.345345-3.123312))))))%1.4e3))))+sin(x%(x*max(sin(6.345345),(rint(6.345345)+y)))/y/1.4e3-y*min((z*3.123312),x));
	}

	private static double fun_36(final double x, final double y, final double z) {
		return pow(23.43e-03,(y%((pow((min(z%x,23.43e-03)%1.4e3-(max((pow(rint(cos(abs(min(23.43e-03*abs(y),sin(3.123312)/x)))),3.123312)%(cos(rint(y))/x)),sin((23.43e-03*23.43e-03+abs(max(abs((x*cos(abs(y))*x*3.123312)),x))))*(sin(abs(((rint(cos(x))%z)%z-3.123312)))+z)/1.4e3/cos(min(1.4e3,min(max(23.43e-03,6.345345),abs(pow(6.345345,z))))+3.123312*cos(max(abs(z),rint(max(x,(1.4e3-sin(x)+(min(y,23.43e-03)*1.4e3))))))+z))+1.4e3+y%y)),cos(1.4e3))%3.123312)+1.4e3)));
	}

	private static double fun_37(final double x, final double y, final double z) {
		return (abs(rint(3.123312))-pow(1.4e3,abs((1.4e3+rint(pow((1.4e3+min((1.4e3*(abs(((min(23.43e-03,23.43e-03-1.4e3)*6.345345+max((3.123312+cos(6.345345/6.345345)),abs(23.43e-03*z)))+z))-x)),3.123312/z)),pow(1.4e3+(rint(abs(6.345345))%rint(pow((pow(z,(23.43e-03/max(3.123312,x)))-z),abs((abs(z)*cos(23.43e-03)))))),x)))))));
	}

	private static double fun_38(final double x, final double y, final double z) {
		return (6.345345/abs(rint(1.4e3))+((min(((6.345345/23.43e-03/1.4e3)+23.43e-03),x)*cos(x))*cos(23.43e-03)%rint(min(1.4e3,(max(pow((6.345345%z),max(z*sin((3.123312+23.43e-03))/cos(y/cos(6.345345)*z)*max(cos(z),x)/sin(z)%pow(1.4e3,sin(abs(x))),x+x))-z,min(1.4e3,min(3.123312,pow(y,min(y,x))+sin(23.43e-03))))%cos(6.345345))-z))));
	}

	private static double fun_39(final double x, final double y, final double z) {
		return max((cos(1.4e3)%3.123312-z*max(6.345345,(23.43e-03-pow(min(cos(z),y),3.123312)))%3.123312/abs(23.43e-03)),(max(abs(1.4e3),(y*abs(z%y)))+rint(max(6.345345*(pow(x,(z-z))*3.123312+3.123312),z))/x%pow(abs(sin((x-x+max(z,3.123312)*23.43e-03+max(x,z)/23.43e-03/y))*pow(max(x,x),6.345345)),((max(1.4e3,sin(3.123312/y)-x)+y)-z))));
	}

	private static double fun_40(final double x, final double y, final double z) {
		return (y+((23.43e-03%sin(23.43e-03))*sin(pow((rint(z)-z/1.4e3),z*3.123312)-(sin(abs(3.123312))+pow((pow(abs(min(pow(z,(abs((y-3.123312)/6.345345)-x)),y/1.4e3)),abs(1.4e3))-x),abs(sin(3.123312)))))/min(((y/6.345345)-sin(max(23.43e-03*3.123312*3.123312,z))),1.4e3+x)));
	}

	private static double fun_41(final double x, final double y, final double z) {
		return rint((23.43e-03*pow(max(min(y,pow(sin((abs(6.345345)*y)),sin(23.43e-03))),23.43e-03),(6.345345%z+cos(abs(sin(abs(sin(z))))*y/abs((sin(z)/rint((23.43e-03/(23.43e-03%1.4e3)+3.123312/(y+max(x,y))))))-pow(rint(z),cos((cos(1.4e3)-(cos((3.123312-rint(z))%23.43e-03)+6.345345))))/y)/rint(cos(3.123312))))));
	}

	private static double fun_42(final double x, final double y, final double z) {
		return pow(z,sin((pow(z,6.345345)/pow(max(pow(y,pow(23.43e-03,x)),23.43e-03),pow(pow((rint(y)+cos(sin(x))),cos(rint(min(6.345345,max(3.123312,(max(pow(6.345345,23.43e-03),x)+min(x,cos(3.123312)))))))),abs(6.345345)/(cos(6.345345)%abs(pow(x,(abs(z)/6.345345)))))))));
	}

	private static double fun_43(final double x, final double y, final double z) {
		return max(min(abs(abs(pow(23.43e-03%(23.43e-03*(z/6.345345+z)),(abs(3.123312)/6.345345%min(23.43e-03,z))))),(((z%max(23.43e-03,sin(rint(y)*x)))*x)+6.345345*sin(abs(((min(x,(x/abs(z)))-(rint(pow((sin(rint(cos(rint(3.123312))))/x)*z/(1.4e3+z),x))%(abs((23.43e-03%z))-3.123312)/1.4e3))+max((23.43e-03-abs(3.123312))*sin(y),x)))))),x);
	}

	private static double fun_44(final double x, final double y, final double z) {
		return cos(min(max((rint(z)/rint((sin((23.43e-03/x/z*pow(y,min(z,x%y))))%(cos((rint(max(3.123312,23.43e-03)+z)/6.345345))-x*x*3.123312/(x*23.43e-03))+pow((abs(cos(23.43e-03))/x),z+min(6.345345,(z%rint(23.43e-03)*3.123312-min(3.123312,cos(sin(cos(3.123312)%(cos(y)*x))))*rint(6.345345)*1.4e3)-y))*z))%y-y/z),(1.4e3/6.345345)),3.123312));
	}

	private static double fun_45(final double x, final double y, final double z) {
		return max(sin(1.4e3),pow(pow(max(sin(max((((23.43e-03-cos(x)/cos(pow(sin(rint((z%3.123312))),z)+y)/z+(x-(abs(pow(x,rint(3.123312)+x))-x)))-cos((sin((6.345345*(max(y,(max(y,x)/3.123312))/3.123312)))%abs(3.123312))-23.43e-03))*x),cos(y))%23.43e-03),6.345345),6.345345),((1.4e3-1.4e3)/pow(cos(3.123312),23.43e-03))))%1.4e3;
	}

	private static double fun_46(final double x, final double y, final double z) {
		return (x%(min((x*sin(23.43e-03)%x),x)*1.4e3*(1.4e3%y/abs(23.43e-03))%pow((cos(y)/23.43e-03),6.345345))/pow(6.345345,(cos(rint(23.43e-03/cos(abs(pow(23.43e-03,rint(x))))-min(1.4e3+y,x)))*(z%sin(rint(x*rint((abs(23.43e-03)-z)))%sin(y-(23.43e-03-cos(3.123312))/y)))/rint(23.43e-03)))%1.4e3);
	}

	private static double fun_47(final double x, final double y, final double z) {
		return rint((6.345345-(((rint((x/y)/(y/x))*pow((max(x,(max(pow(rint(3.123312*(3.123312%x))%cos(rint(1.4e3)),y),(pow((1.4e3/cos(23.43e-03*z)),((y+y)+y))-rint(abs(23.43e-03))/3.123312))+pow(23.43e-03-min((pow(3.123312,(3.123312/y%1.4e3+y))%z%y),3.123312)*(z*23.43e-03-z),z)))%y),23.43e-03))%1.4e3)+((abs(pow(z,(abs(1.4e3)/x/1.4e3)))-3.123312)%6.345345*1.4e3)+(1.4e3%(1.4e3%3.123312*(z/z))*rint(1.4e3)-23.43e-03))))/3.123312;
	}

	private static double fun_48(final double x, final double y, final double z) {
		return rint(pow(z,pow(3.123312,1.4e3-pow(rint(cos(min(max(z,pow(3.123312,23.43e-03)),abs(x)))%y),(y-((z+abs(max(min(3.123312,(6.345345/y)),(max(6.345345%6.345345,y)/max(3.123312,min(3.123312,sin(rint(23.43e-03)))))/6.345345)))-y/((rint((sin(x)+6.345345*sin(sin(max(x,max(1.4e3,y))))))%rint(y)*abs(z))+x)))))+3.123312*1.4e3));
	}

	private static double fun_49(final double x, final double y, final double z) {
		return (sin((y+x))*x)+(6.345345%((x%1.4e3+pow(pow((23.43e-03/3.123312),min((6.345345+3.123312%sin(23.43e-03)/max(6.345345,6.345345))%1.4e3/(z*(rint(z)/3.123312)),(cos(rint(x))%min(z,cos(abs((z/x))))%abs(cos(z))))-z-y),(pow(3.123312,abs(z))*rint(sin((y*min(min(6.345345+max(cos(max(23.43e-03,23.43e-03)),z/min(3.123312,pow((1.4e3/x),z))),abs(23.43e-03))%(z+1.4e3),max(z,x)))*23.43e-03))%sin(1.4e3))))%6.345345))*z;
	}

	private static double fun_50(final double x, final double y, final double z) {
		return ((rint((cos(pow(pow((y/((23.43e-03%pow(6.345345,3.123312)*z)+max(abs(x),23.43e-03)+x)),6.345345),max(z,y)))-(1.4e3+pow(3.123312,rint(sin(x))))))-abs(z)%cos((x/cos(rint(z))-min((sin(x)+pow(23.43e-03,sin(rint(x)))),23.43e-03))/6.345345))-rint(cos(z)))%y;
	}

	private static double fun_51(final double x, final double y, final double z) {
		return (min(min(max(rint(1.4e3),(y/z)),y)+x,abs(((z*(3.123312+z-(sin(cos((3.123312*(sin(z)%y)-rint(x)+z)))/min((rint((1.4e3%sin(x))/y+abs(1.4e3))+x),max((23.43e-03%z),1.4e3))))%min(1.4e3,y)+y)-1.4e3)))/(6.345345+abs(1.4e3)+sin(z%y-abs(z)%(1.4e3*23.43e-03))))+3.123312/23.43e-03;
	}

	private static double fun_52(final double x, final double y, final double z) {
		return sin(sin(23.43e-03)*pow(abs((abs(6.345345/rint(z))-abs(max((3.123312/x),cos(1.4e3%(abs(x)-y-x))/rint(max((z/(pow(min(cos((cos(abs(min(sin((3.123312%6.345345)),sin((z*max(23.43e-03,min(max(rint(rint(min(z,z))),sin(max(x,23.43e-03))),abs(y)))*((x*y)%pow(sin(z),z)+y))))))%cos(max(sin(z),6.345345))-z)),y),z)%6.345345)),6.345345)))))),(rint(x)*abs(3.123312))%rint(abs((23.43e-03*(z-rint(rint(1.4e3))))))));
	}

	private static double fun_53(final double x, final double y, final double z) {
		return (z%pow(sin(y)-sin(max(x,(1.4e3-y)/x))*x*z,(cos(sin(sin(rint((((min((x%23.43e-03-sin(y))-1.4e3/y,y/(23.43e-03*abs(max(x,cos((23.43e-03/6.345345))))+6.345345+sin(y)))/6.345345+3.123312)/max(z,pow(cos(6.345345),3.123312)))+rint(min(z,(rint(abs(min(y,(cos(6.345345)-rint(y)))))*y)))/3.123312)))))%y)));
	}

	private static double fun_54(final double x, final double y, final double z) {
		return pow(y*((y*sin(6.345345))%pow(3.123312,max(max(23.43e-03,z+abs(rint((3.123312%abs((min(6.345345,3.123312)+y)))))),3.123312%((6.345345/6.345345)*sin(sin(x))/y%(6.345345/abs(sin((23.43e-03%cos(y%1.4e3-cos(23.43e-03))*(1.4e3/(y-3.123312)))))))))),abs((1.4e3-min(abs(y),3.123312)))*pow(y,3.123312)-23.43e-03+abs(3.123312)*x+sin((1.4e3-abs((x-rint(z))))));
	}

	private static double fun_55(final double x, final double y, final double z) {
		return sin((pow(6.345345,abs(pow(3.123312,cos(y))-min(3.123312,max(6.345345,6.345345))))%x)%abs(pow(23.43e-03,pow(pow((x%pow((23.43e-03%(cos(y)*z)),sin(rint(3.123312)))*z),y%(abs(cos(1.4e3))%(1.4e3-(y+z)))+(y*3.123312*z+pow(((y%min(y,pow(3.123312,23.43e-03)))%y%rint(z)%(z+6.345345)),y)))%x*pow(rint(23.43e-03),y)+x,x%rint(z))*max(sin(1.4e3),3.123312)-x%3.123312/3.123312%rint(sin(1.4e3)))));
	}

	private static double fun_56(final double x, final double y, final double z) {
		return max(((6.345345%pow(6.345345,1.4e3))-6.345345),max(max(6.345345,min(max(1.4e3,max(1.4e3,pow(6.345345,(3.123312%6.345345-max(z*z,23.43e-03)-max(x,3.123312-(x-(pow(3.123312-sin(abs(x)),3.123312)/y)))/23.43e-03+min(x,abs(sin(1.4e3))))*(z-y)))),3.123312-y)),1.4e3));
	}

	private static double fun_57(final double x, final double y, final double z) {
		return abs((23.43e-03%(cos(max(abs(abs(max(23.43e-03,pow((max(z,min((x*z),min(y*3.123312,(3.123312*6.345345))))*z-(x-1.4e3%max(min(x,min(23.43e-03,(3.123312%max(pow(z,(3.123312*x)),3.123312%y)))*abs((3.123312-abs(max((y%(y/((sin(x)-sin(1.4e3))+1.4e3))),23.43e-03))))),abs(23.43e-03)))),abs((sin(x)%max(cos(rint(z)),3.123312*min(max(y,abs(((6.345345*x)*cos(3.123312)))%(6.345345-min(y,y))),x))+y))%cos(z))))),3.123312))+x)/sin(23.43e-03)))-23.43e-03;
	}

	private static double fun_58(final double x, final double y, final double z) {
		return cos(((min(y*z,((cos((6.345345*z))%(y%cos(x)-cos(3.123312))%max(z,x))+sin(6.345345)+1.4e3+y))/23.43e-03)%sin((y*(6.345345%cos(x))%x/pow(23.43e-03-y,rint(pow(sin(min(3.123312,3.123312)/(sin(3.123312)%rint((min((z/x),(1.4e3+1.4e3))%1.4e3*(y/x))))+23.43e-03)/z,abs(min(x,cos(max(rint(cos(y)),23.43e-03)))))))))));
	}

	private static double fun_59(final double x, final double y, final double z) {
		return min(y,pow((6.345345-23.43e-03/(6.345345/x)),(3.123312%(min(abs(1.4e3),max(1.4e3*(6.345345+max(y,y)),6.345345))*((y+1.4e3)+1.4e3-max((sin((rint(y-max(abs(sin(min(z,3.123312))),pow((pow(y,z)*z*pow(max(max(abs(6.345345),x),y*pow(z,z)%z),6.345345)),sin(sin(x)/6.345345))))*6.345345))/6.345345),z))))));
	}

	private static double fun_60(final double x, final double y, final double z) {
		return min((z%max(3.123312,((y/z)/abs(abs(z)))*min(pow(sin(abs((abs(((y/y/pow(y,3.123312)/z+sin(rint(y))+1.4e3%(sin((z+y))%23.43e-03))-x))-((x+pow(3.123312,cos(min((23.43e-03*6.345345),cos(6.345345)))))%((y%rint(cos((6.345345/(max(sin(y),1.4e3)%sin(cos((23.43e-03%1.4e3))))-6.345345))))%x))-(y*cos(y)))))%(1.4e3%1.4e3),(y/x)%1.4e3),z))),y);
	}

	private static double fun_61(final double x, final double y, final double z) {
		return rint((max(y,max(sin((z+min(z,pow(z+1.4e3,abs((3.123312/(abs((6.345345%((1.4e3/y)/(x*cos(3.123312)*rint(pow(3.123312,rint(min(sin(max(y,z)),(rint(abs(x))*z))))))/((6.345345%abs(y-z)/z)%z))))/23.43e-03)%(6.345345/(23.43e-03/min(rint(23.43e-03*x-y),x))))+pow(y,sin(6.345345*1.4e3)))/y)))),max(sin(z),(23.43e-03+abs(6.345345)))))*y));
	}

	private static double fun_62(final double x, final double y, final double z) {
		return (abs(max(6.345345,min(y,(abs((x/z))/z))*pow(23.43e-03,6.345345)))*z+y/pow(sin(rint(min(3.123312,pow(abs(3.123312),(x%rint((1.4e3*23.43e-03)%1.4e3)))%(rint(z)-max((1.4e3+cos((min(3.123312,6.345345-y+23.43e-03%1.4e3)-y))),(6.345345+y)))))),6.345345)/y)-cos(3.123312);
	}

	private static double fun_63(final double x, final double y, final double z) {
		return (max(y,cos(min(x,rint(pow(sin(rint(z)),6.345345)))))+(max(sin(23.43e-03),rint(z)%y)/min(sin(1.4e3),(y/23.43e-03-abs(pow(pow((pow(23.43e-03,23.43e-03)/(z*min(3.123312,max(23.43e-03,x)%cos(1.4e3)))),pow(23.43e-03,3.123312*6.345345)),y)%z)))))-y+3.123312;
	}

	private static double fun_64(final double x, final double y, final double z) {
		return min(abs(max((((x%pow(sin(3.123312),3.123312))+z)+abs(pow(rint(1.4e3),max(max((3.123312-x-y),(23.43e-03+rint(pow(3.123312,1.4e3)))),rint(max(1.4e3,23.43e-03)))))/23.43e-03),sin((((z/x%y+z-6.345345)%1.4e3%(z%rint(1.4e3+1.4e3))%23.43e-03)-(min(23.43e-03,x)+1.4e3)))/pow(pow(6.345345,x),23.43e-03))),rint(rint(y)));
	}

	private static double fun_65(final double x, final double y, final double z) {
		return cos(cos(pow(pow(min(z,3.123312),((sin(cos((max(23.43e-03,x)-min((rint(sin(z))+1.4e3),max(y,23.43e-03)))/x))/x-z+6.345345)*abs(sin(pow(min(x,((y/max(x,(x/rint(23.43e-03-rint(3.123312)/y))))*max(y,max(23.43e-03,max(x,3.123312)*sin(6.345345))))),y%1.4e3+y)-(1.4e3-((sin(cos(pow((abs(23.43e-03)+x-1.4e3)+y,z)))-z/y-y)+23.43e-03)))))),(z*23.43e-03)))+(3.123312-(cos(23.43e-03)%6.345345)));
	}

	private static double fun_66(final double x, final double y, final double z) {
		return max(abs(rint(pow(x*abs(((cos(3.123312)%min(rint(pow((z+rint(1.4e3)/abs(6.345345)/rint(3.123312)/y),max(min(23.43e-03%sin(1.4e3)*max(3.123312,max((23.43e-03%cos(23.43e-03)),abs(y)))+6.345345,x),abs(z)))+3.123312),6.345345))*3.123312)),(z%x)))),z*y/1.4e3);
	}

	private static double fun_67(final double x, final double y, final double z) {
		return ((pow(y,(x-max(z+sin(x)*rint(6.345345)+min(cos(y),(cos(y)/3.123312*(min(6.345345,x)-y)%abs((max(cos(rint(pow(23.43e-03,23.43e-03))),rint(23.43e-03+6.345345))*pow((x-cos(x)),sin(max(23.43e-03,6.345345))))))),x)%z/z*(cos((x+23.43e-03-x%pow(3.123312,x)))*6.345345)))-(z%6.345345))/abs(6.345345%rint(rint(sin(z)))));
	}

	private static double fun_68(final double x, final double y, final double z) {
		return y/min((cos(6.345345)-x),max(pow(x,(23.43e-03-x+z)),(min((sin(rint(x))*3.123312+(cos(1.4e3)/min(pow(min(z,6.345345),y),(min(23.43e-03,z/cos(23.43e-03)-pow(pow(rint(3.123312/min(3.123312,rint(23.43e-03))+y),y),sin(1.4e3)))+3.123312))+abs(x))),y/max(23.43e-03,y)/x/3.123312/3.123312*rint(1.4e3))/6.345345)));
	}

	private static double fun_69(final double x, final double y, final double z) {
		return max(3.123312,(max(max(pow(3.123312,abs(y)),pow(max(((6.345345%23.43e-03)*x),1.4e3),abs((23.43e-03/3.123312))%y/1.4e3)),(z%((sin(min(23.43e-03,min((3.123312*(cos(((z*1.4e3)%rint(sin(abs(min(y,(z%max(cos(3.123312+1.4e3*y),23.43e-03))))))))-max(x,3.123312*y))),abs(z))))%cos((pow(x,(pow(max(abs(23.43e-03),(1.4e3%3.123312)),abs(y))*pow(((x+y)-cos(3.123312)/cos(6.345345)),x)/pow(x,6.345345)))%pow(y,23.43e-03))))+pow(6.345345,z))))%(y/3.123312/min((z+x*23.43e-03),rint(23.43e-03))*pow(23.43e-03+y,(1.4e3-max(z,z)%y)))))*abs(6.345345);
	}

	private static double fun_70(final double x, final double y, final double z) {
		return abs(max(6.345345,cos(min(z,(y+abs(pow(pow(sin(x/23.43e-03%z+(3.123312/3.123312)%cos((cos((6.345345*abs(3.123312)))+3.123312))+3.123312),y),23.43e-03%(abs(1.4e3)+1.4e3)))))+pow(cos(3.123312*rint(pow(cos(23.43e-03),23.43e-03)/z/z-z%3.123312))*23.43e-03,(1.4e3+23.43e-03))/23.43e-03)));
	}

	private static double fun_71(final double x, final double y, final double z) {
		return cos(x)*max(max((z%abs((3.123312/max(y,x))*23.43e-03)),min(sin(3.123312)/3.123312,max(sin((z-(sin(sin(3.123312))*cos(z)*max(z,pow(x,z))%3.123312))),abs(((x/23.43e-03-23.43e-03)%min(1.4e3,1.4e3/rint((z-min(rint(y),3.123312))))))/(z%(pow(1.4e3,x)%abs(abs(3.123312)))+23.43e-03)))),pow(3.123312,min(y,cos(y))));
	}

	private static double fun_72(final double x, final double y, final double z) {
		return max(pow(rint(23.43e-03),y),(pow(x,z)/max(z,z)-min((((x+6.345345)+max(cos(abs(x)),(max(1.4e3/(x*y),pow(((sin(3.123312)-sin(rint(abs(y))))-(23.43e-03-x)),x/(rint(min(y,6.345345))+x)))*sin(abs(cos(23.43e-03))))))-sin(rint(cos(min(1.4e3,(abs(3.123312)*(z-y)))+(min(23.43e-03,y)/1.4e3))))),x)));
	}

	private static double fun_73(final double x, final double y, final double z) {
		return rint(min(y,(min(x-sin(y/((x*3.123312+cos(min((y-(y*min(max(abs(min(y,((sin(z)-6.345345)-1.4e3))),sin(y)),z)))%min(y%1.4e3/max(rint(23.43e-03),(z+1.4e3))-1.4e3,pow(z,6.345345)),6.345345)))*3.123312)),(min((23.43e-03*min((1.4e3+abs(y)),6.345345)-pow(3.123312,x)),z)+y))%cos(3.123312))));
	}

	private static double fun_74(final double x, final double y, final double z) {
		return 1.4e3%(abs(min(y,max(3.123312*3.123312-(min(abs(rint((((sin((z-pow(min(rint(((abs(1.4e3+(1.4e3+sin(y*y)))-rint(6.345345))%rint(23.43e-03))-(z-3.123312))+max(3.123312,pow(z-1.4e3,pow(6.345345,23.43e-03)))-x%y-6.345345,(abs(x)-1.4e3)),3.123312)))*(z%y*max(z,(max(sin(3.123312),1.4e3)*abs(abs(3.123312))))/y%3.123312))-min(rint(cos((y+(y*x)))),6.345345%6.345345))%rint(min(3.123312,abs(y)/(x+x)))))),(sin(y)+23.43e-03))+x),abs(y%6.345345+3.123312))))*z);
	}

	private static double fun_75(final double x, final double y, final double z) {
		return pow(z,pow((pow(max(23.43e-03,sin(3.123312)),6.345345)*6.345345),(pow(sin(pow(abs(x),y)),(sin(((3.123312+max(23.43e-03,abs(3.123312)+pow((x*1.4e3%y),(x*(abs(cos(3.123312))/3.123312))*min(6.345345,(3.123312-y)))+23.43e-03/min(z,(3.123312/abs(pow(3.123312,x))%23.43e-03))%1.4e3))*(rint(3.123312)+y)/1.4e3*y))*pow(1.4e3,(pow(1.4e3,z)%(abs(max(min(sin(z),y),abs(6.345345)))%3.123312)/abs(1.4e3)))))/y)));
	}

	private static double fun_76(final double x, final double y, final double z) {
		return (z+((abs(abs(sin(23.43e-03))%min((sin(1.4e3)-x),sin(6.345345/max(max(23.43e-03,sin(pow(sin(3.123312),23.43e-03-x))),abs(x)))))/(rint(x%6.345345)/(max(1.4e3,z)/6.345345)))*23.43e-03+abs(3.123312%rint(max(rint((y%rint(y)-z)),(z-rint(max(z,23.43e-03)*max(sin(23.43e-03),(3.123312+6.345345)))+z))))));
	}

	private static double fun_77(final double x, final double y, final double z) {
		return min((23.43e-03+z*rint(1.4e3)+y)%y%abs(min(23.43e-03,min(x,23.43e-03))),((z-y)-z*(rint((z%min(6.345345,pow(pow(rint(y)%3.123312,1.4e3),z)))/pow((abs(1.4e3)-(pow(6.345345%3.123312,y)-z)),min(cos(3.123312-3.123312),(3.123312+z))))/cos((z%1.4e3/23.43e-03)))-y)-(6.345345*x)*6.345345);
	}

	private static double fun_78(final double x, final double y, final double z) {
		return y-abs(min((min((max(y,pow(y,(y/rint(3.123312))))+max(3.123312,pow(1.4e3,cos(rint(sin(x)/max(pow((pow(sin(min(max(y,rint(pow(((3.123312-23.43e-03/y)+(y+cos(sin(rint(y))))),z-(min((23.43e-03*z),23.43e-03)*(y-((pow(x,abs(x))+pow(y,max(sin((23.43e-03%x)),cos((cos(z)*abs(6.345345)-y)))))-y)))))),x)),23.43e-03)-pow(x,pow(z,sin(y%y))))-z%max(max(1.4e3,abs(max(1.4e3,pow(x,pow(abs(min((23.43e-03%max(3.123312,sin(((6.345345/23.43e-03)+3.123312/x)))),1.4e3)),max(6.345345*(1.4e3+z)/(y*z)/sin(6.345345),min(max(x,sin(abs(sin(x)))),z)+rint(x-1.4e3%3.123312%abs(x))%(min(cos(sin(z)),x)%y))))))),((sin(x)*1.4e3)*abs(23.43e-03))),6.345345),max((3.123312*y),1.4e3)))*min(z,x)*((rint(max(6.345345,(min(x,1.4e3)*cos(sin(6.345345)))))%x)*3.123312))))%min(y,cos(x))),rint(z)-z)+z),y));
	}

	private static double fun_79(final double x, final double y, final double z) {
		return abs(abs(rint(cos(y+(6.345345%(rint(cos((pow(cos(pow((min(cos((sin(23.43e-03)-sin(cos(y%1.4e3))+(6.345345-y+abs(z)))),pow(x,6.345345))-cos((1.4e3%6.345345))),y)),min(y,(abs(abs(6.345345)%z)%z)%3.123312*1.4e3))/max((cos(1.4e3/6.345345)-cos(6.345345)),y))))%pow(23.43e-03,cos(23.43e-03))+(1.4e3-x))+y)))));
	}

	private static double fun_80(final double x, final double y, final double z) {
		return max(max(z,1.4e3),min((rint(6.345345)-abs(min((z-6.345345),1.4e3))),1.4e3/pow(3.123312,(min(pow((23.43e-03%z-rint(abs(x))),cos((cos(z-z)%cos(sin(cos(x)))))-1.4e3),pow(x,y))+(x%max(abs(23.43e-03),(3.123312-(3.123312%y)+min(min(x-(y*min((x-y),1.4e3)%x-cos(z)%z+x),abs(23.43e-03)),23.43e-03))-pow(sin(6.345345/y),x))+6.345345*x-23.43e-03)))));
	}

	private static double fun_81(final double x, final double y, final double z) {
		return ((sin(y)-(pow(1.4e3,(23.43e-03*cos(rint(23.43e-03))))-min(x,23.43e-03))*min((1.4e3%max(cos(23.43e-03),pow(rint(1.4e3),6.345345))*((rint(23.43e-03)/(23.43e-03%pow((cos(rint(abs(y)))*max((y-sin(sin(y))),x)),(3.123312%1.4e3))))+min(max(23.43e-03,x),x))),(6.345345-x)))-23.43e-03)/23.43e-03;
	}

	private static double fun_82(final double x, final double y, final double z) {
		return min(z,min(y,abs((pow(y,(x*pow(abs(cos((x*23.43e-03)-pow(z,x/3.123312-z)-pow(x,max(23.43e-03-3.123312,3.123312))/cos(x))),rint(y*max(3.123312,6.345345))-z-6.345345*abs(1.4e3+rint(x)))%max(y,sin(x%pow(3.123312*6.345345/abs(z)%x-x,(pow((pow(sin(6.345345),rint(z))%1.4e3)-abs(6.345345)*rint(z),(cos(z)/3.123312))*x)))/x)))*rint(y)))));
	}

	private static double fun_83(final double x, final double y, final double z) {
		return (6.345345*(y-6.345345+pow(6.345345/abs(1.4e3)/(abs(1.4e3)-min(x,min(y%(x*min(y,sin((((6.345345/(z/6.345345)/23.43e-03)-max(3.123312,sin(y/x))+pow(rint(max(min(3.123312,6.345345),z)),(z+6.345345)))%3.123312)-1.4e3))),cos(rint(z))))),y)-pow(3.123312,z)))%x;
	}

	private static double fun_84(final double x, final double y, final double z) {
		return min(pow(pow((max(pow((6.345345-(z%(z/(y/y*z)*max(abs(1.4e3),x)))-y),(y*pow((z*x),(pow(max(x,1.4e3),abs(pow((1.4e3%6.345345),y)))-3.123312%6.345345)%min(z,(23.43e-03%abs(abs(min(23.43e-03,(min(1.4e3,z)%z/y+sin(6.345345)))))-min(x,max(3.123312,x))-6.345345))/3.123312))),x%z%abs(1.4e3))/x+abs(1.4e3))*(23.43e-03+y),23.43e-03),sin(pow(max(sin(sin((3.123312+6.345345))),23.43e-03),sin(min(x,1.4e3)-3.123312)))),cos(max(23.43e-03,6.345345/3.123312))+3.123312/min(z,6.345345));
	}

	private static double fun_85(final double x, final double y, final double z) {
		return rint(((max(max((23.43e-03%(abs(6.345345)*(3.123312*z))),sin(1.4e3)),(x-23.43e-03/sin(1.4e3)))-z)*(cos(pow((min((y/1.4e3+rint(sin(min(6.345345,3.123312))/1.4e3))+x,(6.345345/max(pow(23.43e-03,min(z,y))+23.43e-03,3.123312)))%x),pow(23.43e-03,y)))*sin(3.123312)-x*3.123312)))/pow(x,23.43e-03);
	}

	private static double fun_86(final double x, final double y, final double z) {
		return ((y-max(rint((z+pow(cos(23.43e-03),(x%z-(y-(y/(1.4e3%pow(pow(pow(23.43e-03,min(23.43e-03/abs(1.4e3),x*rint(3.123312)*sin(1.4e3)+cos(z))),rint((y*23.43e-03))),(z*6.345345%cos(1.4e3))))+23.43e-03))))))%x,pow(6.345345*abs(1.4e3)+abs((1.4e3/3.123312)),1.4e3)))+6.345345*1.4e3)%x;
	}

	private static double fun_87(final double x, final double y, final double z) {
		return max(pow(z-23.43e-03+x,rint(cos(abs(y)))+cos(y+sin((x*min((x*x),pow(y,sin(6.345345+x)+sin((max(rint(x),cos(cos(23.43e-03)))%rint(1.4e3)))))-cos((6.345345*y%cos(x))))*z%y))*pow(((sin(1.4e3)-abs(rint((min(min(3.123312,y),1.4e3)%x))))%(((cos(rint(23.43e-03))/max(3.123312,x/sin(23.43e-03)-x))-x)%(x/cos(sin(y)))))%1.4e3,min((max(pow(y,23.43e-03),23.43e-03)-3.123312)/z,x))),y);
	}

	private static double fun_88(final double x, final double y, final double z) {
		return (abs(pow(sin(cos(x)),pow((rint((z+abs((3.123312*(y-cos(min(y,(x%x-(y+1.4e3)))))))))+x),pow(rint(z+min((x-3.123312),x)),6.345345)/pow((1.4e3-pow(y,6.345345)),6.345345)*3.123312)))+pow((z-pow(1.4e3,pow(3.123312,(6.345345+x-cos(rint(sin(pow(x,z)))-min(y,(1.4e3/(y%abs(cos(pow(y,(z*3.123312))))*sin(max(((pow(pow(pow(1.4e3%max(y,z),3.123312),x)+abs(y),max(y,y-3.123312)+y)/3.123312+y-((x*x)-z)*(6.345345%23.43e-03)/z)+max(max(cos(max(y,max(abs(max(x%(cos(1.4e3)/x),1.4e3)),(max((max(abs((y%y))+3.123312,23.43e-03%max(3.123312+x,23.43e-03))%(y-sin(6.345345))%6.345345),3.123312)-1.4e3)))),6.345345),y)),x)))*min((x%6.345345),3.123312)))/3.123312)%min((23.43e-03/1.4e3),23.43e-03))))),23.43e-03-1.4e3-abs((z-sin(23.43e-03+rint(y))))/y));
	}

	private static double fun_89(final double x, final double y, final double z) {
		return max(x,min(min(max(abs((6.345345/min(1.4e3/1.4e3*3.123312/y,pow((x-1.4e3),23.43e-03))))*max(max(6.345345,(1.4e3%pow(pow(pow(abs(z),y*x)%y*6.345345,3.123312),rint(x)+sin(sin(cos(abs(sin(((6.345345/cos(23.43e-03))/x))))))))),6.345345),rint(pow(z,rint(y)))),x),23.43e-03));
	}

	private static double fun_90(final double x, final double y, final double z) {
		return sin(max(1.4e3+y*cos(z)*z,(3.123312*pow(cos(y/3.123312),abs(z/rint(abs(pow(3.123312*sin(23.43e-03),max(((sin(1.4e3)+min(6.345345,rint(abs(y+sin(pow(x,abs(6.345345)))))))*((max(y,(y*y))%6.345345)+x+1.4e3)),abs(23.43e-03))-(6.345345*abs(abs(y)))))/pow(pow(x,23.43e-03),3.123312)*cos(sin(y))))))))%max(23.43e-03,x);
	}

	private static double fun_91(final double x, final double y, final double z) {
		return pow((pow(min(sin(x),cos(sin(3.123312))),23.43e-03)-min(pow(((6.345345%pow(rint(6.345345),min(pow(x,rint(z)),1.4e3))*23.43e-03)+sin(3.123312))+sin(pow((sin(((z-rint((23.43e-03*min(z,1.4e3)%y)))/6.345345)*(max(rint(z),x)-3.123312))*(z/23.43e-03)*rint(3.123312)),rint(abs(6.345345)%min((1.4e3%23.43e-03*3.123312/sin(z)),6.345345)))),23.43e-03),rint(23.43e-03)-23.43e-03)-23.43e-03),6.345345);
	}

	private static double fun_92(final double x, final double y, final double z) {
		return min(y*(cos(3.123312)*min(y,x)/23.43e-03),y)+sin(((y/((x/y/abs(x)*pow(1.4e3,y-z))*rint(pow(abs(z),23.43e-03)))-(pow(3.123312,(y+z))*1.4e3)/1.4e3)-(y-x%sin(y)%max(1.4e3/(3.123312%(1.4e3%x)),cos(abs(3.123312+3.123312))*x)*23.43e-03+rint(sin(min(x,cos(1.4e3))))))-abs(sin(y)));
	}

	private static double fun_93(final double x, final double y, final double z) {
		return (((rint(1.4e3)%3.123312/(z-(pow(cos(x),abs(3.123312))+z+23.43e-03+y)/y)-abs(1.4e3))-23.43e-03+cos((x-sin(z)%6.345345)/x*23.43e-03))*pow(sin((6.345345%max((x%(z%23.43e-03-(y+((abs(pow(min(max(min(sin(z),23.43e-03),3.123312),pow(3.123312,x))/y,max(3.123312,z)))+1.4e3)*6.345345%x)))),(abs(6.345345)+6.345345/z-1.4e3/3.123312))/x)),(sin(1.4e3)/z/sin(6.345345)))+cos(x));
	}

	private static double fun_94(final double x, final double y, final double z) {
		return min(6.345345,(rint((((23.43e-03%min(max(23.43e-03,(y/3.123312)),(z%y)))/(abs((rint(x)%min((x*rint(23.43e-03/(y*rint(max(23.43e-03,abs(x)))/pow(3.123312,x)))),6.345345)+23.43e-03)%abs((abs(3.123312)-3.123312)))/sin(x+cos(x))-(sin(x)%23.43e-03)))+1.4e3))/pow(z,y)));
	}

	private static double fun_95(final double x, final double y, final double z) {
		return min(3.123312,z/3.123312)%cos(pow((rint(z/cos((sin(y)*z)))/sin(((sin(max(x,1.4e3))+z+abs(z)%3.123312)+6.345345%1.4e3)*((6.345345%rint(z))-((y%6.345345)-pow(x,abs(((6.345345%(sin(x)/(23.43e-03+cos(min((z*x),3.123312)))))*sin(3.123312/abs(z))*rint(z))-x)*(x%abs((23.43e-03%min(z,6.345345*23.43e-03)))))))-pow(max(y,rint(sin(z)+abs(((x/(3.123312+23.43e-03))/23.43e-03))-max(pow(max(23.43e-03,1.4e3),max((z%(6.345345*pow(1.4e3,y))),pow((sin(y)+pow(23.43e-03,z))-x,x))),x)%23.43e-03)),6.345345))*(z%(6.345345%6.345345))),1.4e3));
	}

	private static double fun_96(final double x, final double y, final double z) {
		return pow(cos(max(sin(z),3.123312))-rint(((abs(y)%sin((z*1.4e3)))+sin(y)%abs(max(pow(z,y),(6.345345+cos(x))))))+min(cos(pow(abs(3.123312),min(3.123312,(cos(y)*(3.123312%1.4e3+z)))-((rint(x)*x)*y)/max(y,sin(23.43e-03)))),rint(max((1.4e3%abs(max((rint(x)-max((cos(y-z)+(y-min(max(1.4e3,1.4e3),1.4e3*3.123312))),max(y,sin(x)))*min(z,rint(23.43e-03)))+23.43e-03,1.4e3))),(cos((23.43e-03%1.4e3)%23.43e-03)+sin(z))))),x);
	}

	private static double fun_97(final double x, final double y, final double z) {
		return 6.345345%y+pow(y,min(1.4e3*sin(x%(sin(max(23.43e-03,1.4e3))/abs(sin((y%1.4e3))))+pow(sin(1.4e3),((6.345345/z+z)*pow(y,min(abs(sin(1.4e3)),x)))/(x+x)-(z/(max(23.43e-03/1.4e3,23.43e-03)-abs(rint(max(23.43e-03,rint(1.4e3)))))+pow(z,rint(3.123312))))),min(6.345345,min(23.43e-03,3.123312))%max(6.345345,(23.43e-03/23.43e-03))));
	}

	private static double fun_98(final double x, final double y, final double z) {
		return 6.345345%6.345345%cos((3.123312*z)+(cos((6.345345+max(sin(6.345345),(min((3.123312*pow(1.4e3,x)*abs(y*abs(rint(y)))),y)/6.345345))))%pow(y,min(max(z,min(y,abs(min((23.43e-03%sin((x*rint(cos(3.123312))))),(3.123312*(sin(z-23.43e-03)+cos(x)/(z/(rint(y)/z))*x)))))),3.123312))))%x;
	}

	private static double fun_99(final double x, final double y, final double z) {
		return (((23.43e-03/(6.345345*abs(min(cos(abs(z))+min(1.4e3,((z/1.4e3)-1.4e3+z+pow(6.345345,23.43e-03)/abs(rint(min(z,y))))),min(rint(23.43e-03)%(23.43e-03%3.123312)+z,pow(rint(3.123312),x))))*pow(3.123312,y)-(abs(3.123312)*((max(y,(1.4e3%z))/1.4e3)/(z-min(rint(1.4e3),6.345345)/1.4e3))-max(min(3.123312,abs(23.43e-03)),y))))%(max(cos((max(6.345345,cos((x+(y+3.123312)))+6.345345)*rint((((x-max(3.123312,23.43e-03))-23.43e-03+min(min(sin(3.123312),cos(6.345345)),z))*sin(z)-z)))),(x/pow(pow(z,y),6.345345)))*cos((sin(1.4e3)%(max(min(abs(y),min(min(y,x),y)),y)+23.43e-03)))))%23.43e-03);
	}

	private static double fun_100(final double x, final double y, final double z) {
		return min(x/cos((sin(3.123312)/abs(x)))+x,min((23.43e-03-(y%z)*(1.4e3*(rint(1.4e3*1.4e3)+pow(z,x)+1.4e3))),rint((1.4e3-((((y-(6.345345/max((1.4e3+pow(3.123312,23.43e-03)*pow(1.4e3,rint(max(abs(x),(1.4e3-3.123312))))-y),cos(x))))/(abs(sin(x))%6.345345))*23.43e-03)*y)))));
	}

	private static double fun_101(final double x, final double y, final double z) {
		return (sin(((3.123312/(pow((max(cos(min(y,y)*pow(z,(z-y)))*(z-(1.4e3*(sin(sin(cos(rint(1.4e3))))/pow(max((x%pow(max(3.123312,cos(x-23.43e-03%(3.123312-23.43e-03+x))),((sin(pow(23.43e-03,1.4e3))-abs((sin(z)%6.345345))+sin(3.123312)%y)/abs(y+min(6.345345,3.123312))))+z),max(z,(3.123312+x))),cos((23.43e-03%x)))))),sin(z))/z%6.345345/y)*z*sin(3.123312),(pow(sin(cos(3.123312)),z)/max(y,min(rint((abs((min(((23.43e-03+6.345345)/6.345345),z)-(rint(23.43e-03)%y+23.43e-03)/1.4e3))+3.123312/rint(x))),6.345345/y)*y)))-23.43e-03)/6.345345)%1.4e3)*3.123312)*y);
	}

	private static double fun_102(final double x, final double y, final double z) {
		return (23.43e-03-(y%(abs(pow(min(max(1.4e3,max(x,rint(3.123312))),pow(23.43e-03,y)/23.43e-03+(6.345345%cos(23.43e-03))),(pow(sin((6.345345*max(3.123312,y/z/z*y))),abs((y-cos(abs(abs(z))))-sin(y)))/pow(abs(sin(y)),rint(abs(23.43e-03-3.123312))))))/cos(((23.43e-03+(rint(cos(z)*z)-y))-y)))*y));
	}

	private static double fun_103(final double x, final double y, final double z) {
		return abs((rint(z)-max(((z-y%rint(min(23.43e-03,y)))/abs((min(3.123312-23.43e-03,3.123312)/3.123312))/(23.43e-03/min(y,pow(pow(y,z),max(min(pow(z/6.345345,sin(min(y,y+rint((x*rint(x-6.345345))*rint((z*x))+abs(z)))-6.345345*cos(sin((6.345345*23.43e-03)))-(1.4e3/23.43e-03))),y),1.4e3)))))/abs(x),x)));
	}

	private static double fun_104(final double x, final double y, final double z) {
		return (x/min(z,pow(23.43e-03,min(max(abs(y),x),cos(1.4e3))%3.123312+(pow((z*(max(23.43e-03,sin((cos((6.345345+sin(y)/(x+((max(23.43e-03+z+z%(z-cos(x)),pow(23.43e-03%abs(6.345345),3.123312))*6.345345)/min((abs(cos(cos(23.43e-03)))*x),(abs(3.123312)+max(23.43e-03,3.123312)))))))*z)))%1.4e3)),z)*y%3.123312))))-max(abs(min(y,pow(z,6.345345))),(3.123312*x))/rint(3.123312)+max(x,cos(x)+max(abs(min(23.43e-03,x)),z));
	}

	private static double fun_105(final double x, final double y, final double z) {
		return abs(max(y,(1.4e3/x/((pow(6.345345,pow(y%1.4e3,sin(max(6.345345,1.4e3))))+23.43e-03)*(y*y)))))%max(min(pow(pow(pow(x,(z*1.4e3+max(pow(min(min(6.345345,cos(3.123312)),abs(x)),(3.123312-23.43e-03)),abs(max(1.4e3,y))))),6.345345),3.123312),(3.123312+(min(23.43e-03,z+(min(y,23.43e-03)-1.4e3*max(rint(23.43e-03),(sin(x)/(min(z,max(x,abs(cos(y))))*23.43e-03/z)))/3.123312))+pow(sin(3.123312),y)/x))-y),y+cos(y));
	}

	private static double fun_106(final double x, final double y, final double z) {
		return cos((pow(cos(23.43e-03),z)+sin((pow((min(3.123312,3.123312)+1.4e3*abs(y)),23.43e-03/(x*(abs(rint(1.4e3-sin(pow(1.4e3,1.4e3))-sin(3.123312)))+x+y*1.4e3)+y))-3.123312*23.43e-03-(6.345345+(z+(min(abs((cos(23.43e-03)/3.123312)),23.43e-03)*y)+(23.43e-03/y))*y)*3.123312-y))));
	}

	private static double fun_107(final double x, final double y, final double z) {
		return (abs(6.345345+sin(((rint(sin(cos(max(rint(y),y)%1.4e3)))+max((sin(y)%y),((x-y)%(z/x+3.123312)))/(abs(max(z,y))%cos((max(abs(cos(x)),z)/x)+(23.43e-03/min(6.345345,pow(z,3.123312))/sin(z)))-z)/(max(6.345345,(z-23.43e-03+min(sin(abs(23.43e-03)),((6.345345+rint(y))*x))))-cos(y/y*max(x,3.123312*(x-z))*cos(x)%3.123312))+rint(3.123312))*z))/(x-x))-x);
	}

	private static double fun_108(final double x, final double y, final double z) {
		return (pow(((23.43e-03*1.4e3*sin(6.345345))%min(z,6.345345)-x),(cos(1.4e3)+x))+pow(cos(x),pow(rint((abs(x)/1.4e3)),z))-pow(((1.4e3+max(cos(max(1.4e3,x/z)),z))*(min(6.345345,min(cos(rint(1.4e3)),z))/pow((pow(min((min(rint(sin(x)),x)+x)+y-x,23.43e-03)%max(cos((x-6.345345*max(min(y,cos(abs((((z%y)-pow(sin(sin(z)),23.43e-03/(y-x)))%max(6.345345,rint(x))))))/3.123312-min(3.123312,23.43e-03),(x-x)))),y),max(y*y,rint(6.345345%y)))%6.345345*y),sin(3.123312)/rint(cos(1.4e3))))-23.43e-03%max(y,6.345345)),6.345345));
	}

	private static double fun_109(final double x, final double y, final double z) {
		return max(min(3.123312,(6.345345*(rint(rint(((((6.345345%cos(y*sin(x)))-(y-y))+23.43e-03)*x)))*z%(6.345345-z))))/23.43e-03,min(pow(6.345345,3.123312-x),abs(abs(cos((23.43e-03%((y+y)/6.345345+6.345345*abs((abs(sin(6.345345))/1.4e3)-rint(3.123312)))*sin(abs(cos(((z%1.4e3)%z*y))))/x))+cos(min(3.123312,sin(1.4e3))/z)-1.4e3)))%rint(x*z%min((abs(rint(x))/pow(y,pow(23.43e-03,sin(3.123312)))),min(z,rint(cos(23.43e-03))))));
	}

	private static double fun_110(final double x, final double y, final double z) {
		return rint((z%(((abs(((pow(x,23.43e-03)-3.123312)%max((6.345345%cos(3.123312)+23.43e-03)-abs(rint(y)),(3.123312/pow(x,1.4e3))-1.4e3)))*sin(6.345345%(z-((x/1.4e3)-sin(abs((rint(y)/abs(z)*1.4e3)))*x))+((sin(3.123312-6.345345)-abs(z)-23.43e-03-23.43e-03+6.345345)*1.4e3)))*abs(6.345345)-3.123312)-sin(x))+3.123312));
	}

	private static double fun_111(final double x, final double y, final double z) {
		return max(z,x%(rint(z)*max(23.43e-03-pow(max(6.345345,(pow((rint(pow(cos(z),rint(x)))/3.123312%max(z-(z-pow(23.43e-03,y)%sin(abs((abs(6.345345)%6.345345)))),z)+z),23.43e-03)+abs(z))+1.4e3),abs(y)),1.4e3/(23.43e-03/x)*z)-pow(y,23.43e-03)))%(cos(6.345345%sin(23.43e-03))-z/z-min(6.345345,min(min(y,23.43e-03),(rint(1.4e3)*rint(rint(abs(y)))))));
	}

	private static double fun_112(final double x, final double y, final double z) {
		return abs(max(cos(rint(6.345345))/max(cos(y)%z/abs(rint(abs(cos((sin(max(y,z)%23.43e-03/max(x,x))%1.4e3))%min(3.123312,z)))),(23.43e-03/min(z,6.345345*max(((min((sin(y)/6.345345),(y/23.43e-03)*abs((max((y/23.43e-03),(y*rint(1.4e3)))*pow(sin(1.4e3),min(z,y))))*((3.123312/max((x/1.4e3),abs(23.43e-03+abs(6.345345))))%y))/y)-23.43e-03),6.345345/((3.123312%max(min(1.4e3,max(1.4e3,x)),max(pow(abs(x%1.4e3+abs(y)*23.43e-03),cos(x)),z*y))-pow(z,min(23.43e-03,y)))+(23.43e-03*6.345345-rint((6.345345+sin(max(x,z))/x))))))))*23.43e-03/y,cos(x)));
	}

	private static double fun_113(final double x, final double y, final double z) {
		return pow(rint(cos((((pow(23.43e-03,((abs(z)*1.4e3)*6.345345))/z%3.123312)/1.4e3)/pow((x+z),max((rint(max(23.43e-03,y)+max((pow(sin(x),x)%abs(3.123312)),sin((max(y,(1.4e3+y))/23.43e-03))))*min(x,3.123312)),6.345345))))),(sin(pow(rint(min(sin((max(z,(z+(cos(min(abs(rint((y+abs(abs(cos(3.123312))))))%x,cos(3.123312)))/y)))%(3.123312+pow((cos(rint(3.123312/1.4e3))-23.43e-03)*z,z)+23.43e-03))),y)),1.4e3/23.43e-03))%x+((min(1.4e3,x)+y)+max((x+6.345345)*3.123312,1.4e3))));
	}

	private static double fun_114(final double x, final double y, final double z) {
		return (abs((sin(pow((y-max(1.4e3,3.123312)*pow(y,3.123312)+(3.123312-x))*6.345345%abs(pow(3.123312%23.43e-03,pow(max(rint(rint(rint(sin(1.4e3))))-abs(cos(z)),6.345345),z+rint((min(z,z*sin(abs(y))+(6.345345*6.345345))/3.123312))+x))),(y*6.345345)))+y))%sin(z)*y/sin(23.43e-03*(cos(y)%cos(cos(6.345345)))+1.4e3))*z/sin(1.4e3);
	}

	private static double fun_115(final double x, final double y, final double z) {
		return min((23.43e-03+(6.345345%cos(min(y,min(abs(abs((rint((pow(3.123312,(x/rint(23.43e-03)))*23.43e-03/23.43e-03/x))%z/x*1.4e3-(6.345345*sin((x%23.43e-03%x)))))),(6.345345/(6.345345%1.4e3)/cos((abs(y+sin(y)+x+((1.4e3%z)*23.43e-03))-x)))))))/(sin(z)+abs((x/sin(z))-23.43e-03%6.345345))),z);
	}

	private static double fun_116(final double x, final double y, final double z) {
		return max(cos(sin(1.4e3%((y-1.4e3)-(23.43e-03*abs(max(3.123312,((3.123312+x)%y))))-(x+abs((3.123312/(x-max(23.43e-03,abs(1.4e3))*(max(max((z/1.4e3),6.345345),(abs((((z*z+(min(y,1.4e3)-3.123312))-x)%(z+sin(cos(z/z%6.345345+y)))))/y))-abs(cos(1.4e3)))))))))),((((3.123312-23.43e-03)%y)-abs(y))+abs(abs(y*z%x))-pow(rint(1.4e3),z*3.123312)));
	}

	private static double fun_117(final double x, final double y, final double z) {
		return cos(pow(rint(6.345345)%z,(sin(23.43e-03)+(3.123312-y)+((y+abs(abs((3.123312*6.345345/((max(abs(rint(cos(6.345345)+x)-(23.43e-03-cos(3.123312)+y+((z%max(6.345345,((y/y)+y)/abs(sin(23.43e-03))))-cos(pow(z,6.345345))%z)*(cos(3.123312)*rint(z)/z)+23.43e-03)*23.43e-03),1.4e3)*sin(max(min(y,cos(y))%pow(23.43e-03,(x*cos(cos(cos(6.345345)*(max(3.123312,rint(z))-23.43e-03+z)-(1.4e3%3.123312)))))-((rint(abs(max((3.123312*z),23.43e-03))+cos(x))-z%(z/23.43e-03-z))*z),x)))*(z*3.123312)))+((23.43e-03/z)+(cos(3.123312)*cos(max(1.4e3,y)))))))/x))-cos(min((x/sin((1.4e3*rint(abs((3.123312*y)))))),abs((z-x))*6.345345)*z))+1.4e3);
	}

	private static double fun_118(final double x, final double y, final double z) {
		return cos(max((abs(abs(pow(1.4e3+max(x,cos(1.4e3)),1.4e3)))/cos(min(abs(y),6.345345))),z-(((1.4e3-((pow(y,min(z,pow(pow(6.345345/x,23.43e-03),sin(1.4e3/pow(rint(3.123312)%z,z)))+6.345345-23.43e-03))%sin(sin(((1.4e3-y*cos(max(rint(1.4e3),((3.123312+6.345345)%y))))-(3.123312+sin(6.345345)))))%x)*x-z))%3.123312)/z*sin(23.43e-03)/6.345345)));
	}

	private static double fun_119(final double x, final double y, final double z) {
		return (pow(max(x,z),6.345345+pow(sin(cos(cos(sin(abs(1.4e3))))),cos(3.123312))%(((1.4e3+x+abs(abs(z)))/rint(min(rint((y-3.123312+x)),cos(abs((pow(min(rint(6.345345)-pow(23.43e-03,y),23.43e-03),(sin(x)-rint(rint(y-z))))*3.123312%pow(x*1.4e3/(y*cos(y)),abs(23.43e-03)%rint(6.345345)+1.4e3)))))))/max(min(z,abs((pow(1.4e3,(min(abs(3.123312),x)%3.123312)-z-pow(pow(1.4e3*6.345345/z,cos(abs(3.123312))),6.345345)-3.123312)*6.345345))),z)))/3.123312/z);
	}

	private static double fun_120(final double x, final double y, final double z) {
		return (y+(pow((y-max(x,pow(max(y,(rint(sin(z))*6.345345+6.345345-sin(abs(y+abs(sin(6.345345*23.43e-03))))))*min(z,cos(x/1.4e3)),y%min(23.43e-03/x+3.123312,((x-x)*y*1.4e3+x+y))%pow((min((23.43e-03*(max(z,pow(max(1.4e3,abs(x)),cos(1.4e3)))%23.43e-03)),23.43e-03)+23.43e-03),6.345345))))/max(23.43e-03,z+3.123312),1.4e3)%23.43e-03*max(abs(y)+min(z,1.4e3),1.4e3/sin(pow(sin(6.345345),y)))))%3.123312;
	}

	private static double fun_121(final double x, final double y, final double z) {
		return (abs(min((x-3.123312/rint(sin(23.43e-03))%cos(cos(max(y,y)))),(abs((6.345345+z))/(min(abs(abs(1.4e3)),23.43e-03)-(1.4e3+((1.4e3-x-pow(y-pow(23.43e-03,1.4e3),6.345345)*23.43e-03)/23.43e-03%(z+y)))))))/(cos(max(y,pow(abs(sin(x)),y/y*23.43e-03)-y))*(23.43e-03*abs(min(x,3.123312)))));
	}

	private static double fun_122(final double x, final double y, final double z) {
		return rint(min(1.4e3,((y-y)*cos(sin(pow(3.123312,(y%max(min((cos(y)%3.123312),(sin((3.123312/1.4e3))-rint((x/y))-z%3.123312+y)*y/z),min(rint(pow(y,z)),z)*y-y)+y)))))*abs(z)%3.123312/(abs((6.345345+(x-y)*z/x))%z)))*pow(sin(z),z%23.43e-03/pow(x,23.43e-03)*(sin(cos(x)-x)-23.43e-03))*x;
	}

	private static double fun_123(final double x, final double y, final double z) {
		return sin(sin(((pow(3.123312,z)-23.43e-03)%(y*pow(min(pow(max(6.345345,3.123312),(1.4e3*1.4e3%pow(23.43e-03,z))),y),6.345345/abs(y))+23.43e-03)))%pow(min(23.43e-03,23.43e-03%23.43e-03)-sin(pow(pow(y,(y%(y+abs(3.123312)))),x+y)),3.123312-y)+rint((rint(23.43e-03/z)-23.43e-03)));
	}

	private static double fun_124(final double x, final double y, final double z) {
		return (max(min(sin(sin(abs((rint(pow((3.123312*z),min(6.345345,z)))*23.43e-03)))),pow((x%(sin(x)*rint(min(z,x))*z+(max(1.4e3,(y-y-23.43e-03*cos(6.345345/abs(max(pow(1.4e3,(x/max(abs(max(y,z)),rint(y)))-1.4e3),(1.4e3-cos((23.43e-03-cos(max(23.43e-03,z))/3.123312*x))%pow(1.4e3,23.43e-03)))))))+sin((cos(y)+pow(3.123312,rint(((y%abs(x))-x)))/x)/z)))),z)),x)+23.43e-03);
	}

	private static double fun_125(final double x, final double y, final double z) {
		return (sin(y-(6.345345*cos(((y/23.43e-03)/(6.345345/min(6.345345,sin(6.345345))*abs(23.43e-03-max(min(3.123312,23.43e-03/(max(min(pow(max(z/23.43e-03,3.123312),pow(x,(pow(x,cos(23.43e-03)-23.43e-03%z)*x))),y),min(z,6.345345))%pow(z,3.123312))),max(min((z-min(z,1.4e3)),(x+((((max(3.123312,3.123312)/y)%3.123312)*6.345345)+min(z,23.43e-03))))/6.345345,1.4e3))))))))%x);
	}

	private static double fun_126(final double x, final double y, final double z) {
		return min(pow(max(y+(6.345345-pow(z,3.123312*z)),(z%y))-y,sin(1.4e3)),min((x-z),cos(sin((x-y)%(1.4e3%abs(z%max((z/cos(pow(rint(z),min(y,((3.123312*x)%(max(pow(cos((z*(3.123312%23.43e-03))),23.43e-03),rint(rint(z)))-3.123312))))))%1.4e3-z,3.123312)))*cos(max(6.345345,sin(23.43e-03)))*x))));
	}

	private static double fun_127(final double x, final double y, final double z) {
		return max(23.43e-03,sin(rint((3.123312*max(((x/3.123312)*pow(x,6.345345*6.345345*y)),rint(((1.4e3/max(z,(x*23.43e-03+min(((6.345345-23.43e-03)*(6.345345*3.123312)-z),y%1.4e3)))+x)*max(x,min(max(6.345345,y),sin((pow(sin(6.345345/(max(z%z*3.123312,pow(y,x))*sin(pow((23.43e-03-rint(1.4e3)+x),6.345345)))),y)+x))))-3.123312%(1.4e3%((x+min((sin(x)-23.43e-03)+3.123312%1.4e3-6.345345*abs(y/sin(rint(y)%(sin(1.4e3)+max(rint(z),max(1.4e3,(x*y%cos(min(cos(abs((1.4e3+23.43e-03))),y)))))))),z)*z)/cos(y)))))*1.4e3)))));
	}

	private static double fun_128(final double x, final double y, final double z) {
		return ((pow(min(y,max(y,sin(6.345345))),pow(y,(pow((y*x),max(((3.123312+x)*y),(rint((abs(1.4e3)-y))/(23.43e-03-cos(max((max((1.4e3%sin(abs(cos(3.123312)))),x)%y),z)))))/(x%(23.43e-03+sin(sin(23.43e-03)))/1.4e3-y))-23.43e-03)))+x)-pow((x*1.4e3)*23.43e-03,abs(abs(y))));
	}

	private static double fun_129(final double x, final double y, final double z) {
		return (z/23.43e-03+x/max(y,pow(max(pow(1.4e3,z),3.123312),pow(sin(min((rint(rint(max(z,rint(pow((x+sin(z))*max(max(3.123312,23.43e-03),6.345345),6.345345))+(abs(y)+max(z,1.4e3-rint(x))))))/z+6.345345),min(abs(1.4e3),(1.4e3-cos(sin(23.43e-03))))-cos(y))),1.4e3))));
	}

	private static double fun_130(final double x, final double y, final double z) {
		return (sin(max(1.4e3,pow((abs(max(rint(min(pow((x*23.43e-03%cos(1.4e3)*(y*y)),z),abs(y)))-1.4e3,y))+max(1.4e3,(z+pow(z,max(6.345345,x))))),3.123312))%3.123312)%((pow(23.43e-03/cos(rint(23.43e-03))%x*(3.123312/z),max(z,cos(sin(cos(z)-3.123312))+y)*y)*23.43e-03)*(1.4e3/rint(y)+x)+abs(x)));
	}

	private static double fun_131(final double x, final double y, final double z) {
		return min(6.345345,y)+(abs(z)+6.345345)+6.345345/((3.123312-rint(max(23.43e-03,sin((y-cos(z))))))*y-((z%z)*(rint((1.4e3*(pow((max(y,3.123312)*cos(3.123312)/6.345345/x),abs(x)+23.43e-03/sin(min(cos(23.43e-03)+6.345345,3.123312*z)%1.4e3))+3.123312)))%1.4e3)));
	}

	private static double fun_132(final double x, final double y, final double z) {
		return pow(z,1.4e3%(rint(min(pow(z/(z-pow(max(sin(y),max(3.123312,max(sin(x)-abs(rint(23.43e-03)),max((6.345345/(rint(pow(min(sin(y),3.123312),23.43e-03))*6.345345)),pow(3.123312,y))))),max(23.43e-03,z)))-1.4e3-pow(sin(rint(23.43e-03*(x%23.43e-03)*sin(z))),max(1.4e3,cos(pow(y,3.123312)))),max(1.4e3,y)),y-x)/23.43e-03)-min((6.345345%3.123312),max(max(rint(6.345345+y)*z,z),23.43e-03)-min(cos((abs(abs(6.345345))-abs(23.43e-03))),23.43e-03)%z))*x)-sin(z);
	}

	private static double fun_133(final double x, final double y, final double z) {
		return (cos(max(((6.345345%pow((rint((cos((sin(1.4e3)+z))%abs(y)))*sin(sin((1.4e3*(6.345345+1.4e3))))),abs(6.345345%3.123312)))/23.43e-03),23.43e-03)%min(6.345345+1.4e3,min(sin(pow(3.123312,min(pow(cos((cos(abs(6.345345))*x)),y),3.123312))),3.123312)))*1.4e3);
	}

	private static double fun_134(final double x, final double y, final double z) {
		return 23.43e-03/sin((((abs(sin(rint(max(6.345345,z))-sin(y)))*y+pow((3.123312+x),(rint(((z%6.345345)+(cos((1.4e3+abs(1.4e3)%max(y,3.123312)/pow(max(23.43e-03,z),abs(abs(max(((sin((max(23.43e-03+x,23.43e-03-y)+1.4e3%6.345345/(1.4e3%rint(3.123312))+6.345345))%min(min(23.43e-03,(z/23.43e-03)),(6.345345%x)))/sin(6.345345)),x))))-y))/x)))*y)))/z)%cos(cos((y+23.43e-03%6.345345))-pow(abs(z),max(6.345345*y+max(1.4e3,(abs(3.123312)*6.345345*sin(x))),(23.43e-03+abs(rint(x)))))*min(cos(z),(max((y*23.43e-03),y)+3.123312))/max(max(y,y),23.43e-03)%sin(3.123312))));
	}

	private static double fun_135(final double x, final double y, final double z) {
		return cos(max(min(23.43e-03%z+z%x,(pow(y,6.345345)%(z-min(pow(1.4e3,(cos(z)/z)),min(min(3.123312,abs(23.43e-03)+cos(y)),min(sin(cos((pow(3.123312,1.4e3)-((6.345345*(y*max(cos(x),pow(23.43e-03%min(max(x,abs(3.123312)),(6.345345+(abs(min(max(23.43e-03*(sin(23.43e-03)+y),y)%rint(pow(z,6.345345)),6.345345))/abs(cos(y)))/pow((3.123312-z),sin(z*y)+max(x,x)))),23.43e-03)))-z%3.123312)-y*z)))),1.4e3+y)))))),3.123312));
	}

	private static double fun_136(final double x, final double y, final double z) {
		return cos(max(abs(23.43e-03),(y%(max(pow(abs((y%x)),(rint(rint(sin(sin(min(rint(3.123312),max((23.43e-03-abs(1.4e3)-3.123312+((x*1.4e3)%max(z,y))),max(min(rint(y),abs(6.345345)),sin(3.123312))))))))+max(min(6.345345,y),z))),rint((x/z+z%x)))*23.43e-03/x))));
	}

	private static double fun_137(final double x, final double y, final double z) {
		return (abs(abs(pow((1.4e3%((sin(max(3.123312,23.43e-03))+cos(max(min(z,y-rint(pow(6.345345,min(y,y)))/abs((rint((23.43e-03+(pow((sin(1.4e3)-6.345345+1.4e3),1.4e3)*x-y))-6.345345)*z))),y*sin(x/y)))+y-z)/max((23.43e-03/x/(x-sin(x)-y%6.345345-rint(cos(23.43e-03)))),(6.345345-sin(1.4e3)))%1.4e3))+y*6.345345%23.43e-03+1.4e3-1.4e3,(x%abs(x*1.4e3)%23.43e-03)/x)))%6.345345);
	}

	private static double fun_138(final double x, final double y, final double z) {
		return min(23.43e-03/min(pow(6.345345-y,sin(max(sin((cos(x)-z%rint(min(z,6.345345)))),abs(abs((min((6.345345-(3.123312+abs(max((z/z),max(23.43e-03,max(min(sin(cos(z)),23.43e-03),z))))))*6.345345,z)/max(1.4e3+3.123312/1.4e3,23.43e-03)+(z+rint(z))%max(sin(z),6.345345)-rint(pow(1.4e3,y))*pow(6.345345-abs(z),z)))))+z)),z),3.123312)/6.345345;
	}

	private static double fun_139(final double x, final double y, final double z) {
		return ((sin((3.123312%rint(cos((abs(1.4e3)/sin(23.43e-03))))))+(z*(z%abs((z+(y*6.345345%sin(cos(pow(23.43e-03,y)))-((x-(z%x+cos(23.43e-03)))-min(6.345345,(6.345345*(cos(y)-3.123312)-cos(pow(y,pow(23.43e-03,cos((max(3.123312,1.4e3-6.345345)+1.4e3)))-x))+y))%1.4e3))))+1.4e3)*(x-y)))+z)+6.345345;
	}

	private static double fun_140(final double x, final double y, final double z) {
		return (z/max(min(abs(23.43e-03),min(z,pow((max(y,x)-rint(((6.345345%min(23.43e-03,(23.43e-03%pow(max(abs((min(1.4e3,max(abs(pow(x,z)),abs(23.43e-03*sin(6.345345))))+min(1.4e3,cos(max(23.43e-03,23.43e-03)-z)))),1.4e3),1.4e3))))+23.43e-03%cos(x)))-min(1.4e3,1.4e3-23.43e-03)),rint(23.43e-03)-3.123312))*x),6.345345-pow(cos(min(rint(cos(y)*min(1.4e3-pow(23.43e-03,x)/abs(1.4e3),min(6.345345,z))%x),(6.345345+23.43e-03+max(rint(pow(max(min(z,abs(3.123312))-((y+y)*abs(y)),(z*y)),min(z,(y+3.123312)))/rint(z))*z,1.4e3)))),z)));
	}

	private static double fun_141(final double x, final double y, final double z) {
		return (min(3.123312,max(min(min(z/x,6.345345)+x,min(y,sin(z+z-6.345345+(3.123312-cos(max(abs(1.4e3),cos(min((max(y,(3.123312*cos(y/1.4e3)))*1.4e3),z*23.43e-03))+3.123312)))-x))+cos(((((cos(1.4e3)*23.43e-03)+3.123312*y)%23.43e-03)+x))),max(3.123312,23.43e-03)))*3.123312);
	}

	private static double fun_142(final double x, final double y, final double z) {
		return cos((23.43e-03+((3.123312+(y+abs(cos(x))%(1.4e3/6.345345-3.123312))*abs(3.123312))/min(cos(max(abs(max(sin(min((6.345345+rint(1.4e3)*cos(1.4e3)),abs(abs(z)/rint(y)))),y)),cos(3.123312))),abs((x/x)))-sin((23.43e-03/(sin((cos(z%(x/y))-z-6.345345)-23.43e-03)%rint(cos(rint(x))))))*(6.345345*cos(y)))));
	}

	private static double fun_143(final double x, final double y, final double z) {
		return max(abs(1.4e3),(abs(y)-max(6.345345,min((1.4e3%rint((3.123312-3.123312))),abs((cos(cos(((sin((6.345345%min(6.345345,rint(y-abs(x))))%max(23.43e-03,3.123312))/3.123312)%6.345345)))/(min(cos(pow(23.43e-03,23.43e-03*6.345345)),6.345345)*sin(min(z-y,((x%23.43e-03)+1.4e3)))))))/(min(6.345345,abs(z))-z))%23.43e-03));
	}

	private static double fun_144(final double x, final double y, final double z) {
		return (sin((rint(x)+(cos(z+min(y%abs((max((1.4e3-min(abs((sin(y)-cos(6.345345))),y)),(max(abs((max(z,z)*y)),cos(min(cos(z)-1.4e3,3.123312)))*x))+cos(sin(max(min(y,(y%min(cos((cos(abs(3.123312))*x)),x)/pow(rint(6.345345),sin((x%23.43e-03))*1.4e3/min(6.345345-3.123312,max(sin(6.345345),(min(1.4e3,x)/z+z))))/y)),(abs((23.43e-03%sin(23.43e-03)))%1.4e3%x)))))),pow(3.123312,sin(min(y,(rint(max(6.345345%(abs(cos(pow(abs(pow(x,z))-6.345345,1.4e3*z*rint(rint(6.345345)))))%1.4e3),z))*min(z,23.43e-03)))))))%min(max(z/23.43e-03,z-(1.4e3+3.123312+1.4e3)),sin((23.43e-03+y))))))/pow(z,pow((6.345345*rint(6.345345)*z),pow(sin(sin((23.43e-03+pow(1.4e3,pow(y%max(rint(min(x,23.43e-03)*(1.4e3%x)%abs(max((z/(3.123312-1.4e3)),rint(23.43e-03)))),23.43e-03),x))))),x))));
	}

	private static double fun_145(final double x, final double y, final double z) {
		return (abs((x*1.4e3)/(x%min(((sin(cos(rint(23.43e-03)))*min(min(y%y-6.345345,3.123312)%pow((pow(1.4e3,min(3.123312,6.345345)+(z+z%abs(y)))*z/23.43e-03)%y,pow(x,abs(x)*pow(1.4e3,cos(max(23.43e-03,1.4e3)))))*sin(abs(6.345345)),(max(3.123312,abs(1.4e3))+z)))*min(max(cos(x),1.4e3),y*z)),1.4e3))-6.345345-(23.43e-03+(abs(y)%23.43e-03%(x/min(max(cos(abs(y)),min(rint(3.123312),x)+1.4e3),1.4e3)))))*min(x,(23.43e-03*6.345345)));
	}

	private static double fun_146(final double x, final double y, final double z) {
		return rint(pow(x,6.345345))-pow(sin(abs(min(23.43e-03*y,max(pow(min(y,abs(y)),6.345345)*3.123312,(rint(min(sin((y-rint(z)/abs(x)%max(((23.43e-03-y*max(rint(1.4e3),6.345345))-z),cos(abs(pow(6.345345%3.123312,23.43e-03))/rint(23.43e-03)))%1.4e3)),(min(3.123312,6.345345)*6.345345%1.4e3)))-sin(3.123312)-abs(y)))))+z),rint(6.345345));
	}

	private static double fun_147(final double x, final double y, final double z) {
		return pow(abs(cos(max(y,6.345345))),abs(sin(abs((abs((x%(pow((abs((pow(y,x)-6.345345+max((z%(cos(cos(min(y,23.43e-03)))*x-z)/((x+cos(6.345345))/6.345345*(x%(abs(z)/abs(23.43e-03))))),min(sin(6.345345)/cos(6.345345),sin((pow(y,min(23.43e-03,rint(x)))/max(3.123312,x)))))))/1.4e3%1.4e3%z*3.123312-3.123312)*abs(y/z+1.4e3+sin(rint(max(rint(6.345345-6.345345),3.123312))))*(cos(x)%((z%z*1.4e3)-rint(min(sin(y),x)))),pow(23.43e-03,x))*pow(1.4e3+6.345345,abs(sin(3.123312))))))%z*3.123312/(y*y)%((z+pow(rint(y),y))*6.345345))))));
	}

	private static double fun_148(final double x, final double y, final double z) {
		return sin((1.4e3%max(x,(z-pow(max((x%y)%sin(max(y,abs(y))),x),z)))-x/6.345345/(rint(x/y)%pow(3.123312,rint(cos(1.4e3)+(abs(((1.4e3-6.345345+3.123312)%abs((3.123312/rint(min(3.123312,sin(rint(z))))))+(y/6.345345)+max(rint((rint(y)-z))/3.123312,min(23.43e-03,x))))%cos(cos(6.345345))))))));
	}

	private static double fun_149(final double x, final double y, final double z) {
		return x-(sin((min(y,1.4e3)/max(6.345345/sin(max((min(pow(3.123312,max(x,cos((min(y,cos(z)%abs(x))+y-x*6.345345)))),y)/x),abs(23.43e-03)*abs((((max(y,min(1.4e3,(y+min(cos(sin(x)),1.4e3))))%23.43e-03+3.123312)-y/cos(1.4e3))/3.123312))))/z,(6.345345*x%pow((max(y,z)*cos(z)),pow(z+cos(x),x))))/((3.123312/cos(1.4e3)-abs(max(z,abs(23.43e-03))))-3.123312))*x)-23.43e-03);
	}
}
