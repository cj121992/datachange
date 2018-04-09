package com.melot.data.change.pg.parser;

// Generated from PgLogicalDecoding.g4 by ANTLR 4.5.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PgLogicalDecodingLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, Number=17, 
		Date=18, Time=19, Identifier=20, QuotedString=21;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "Number", 
		"Date", "Time", "Identifier", "QuotedString", "Num", "Char", "Underscore"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'BEGIN '", "'COMMIT '", "' (at '", "')'", "' '", "'table '", "': '", 
		"'INSERT: '", "'UPDATE: old-key: '", "'new-tuple: '", "'UPDATE: '", "'DELETE: '", 
		"'(no-tuple-data)'", "'['", "']:'", "'.'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "Number", "Date", "Time", "Identifier", 
		"QuotedString"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public PgLogicalDecodingLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PgLogicalDecoding.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\27\u00e5\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3"+
		"\20\3\21\3\21\3\22\6\22\u00a8\n\22\r\22\16\22\u00a9\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\6\24\u00c1\n\24\r\24\16\24\u00c2\5\24\u00c5\n\24\3\24"+
		"\3\24\6\24\u00c9\n\24\r\24\16\24\u00ca\3\25\3\25\3\25\6\25\u00d0\n\25"+
		"\r\25\16\25\u00d1\3\26\3\26\7\26\u00d6\n\26\f\26\16\26\u00d9\13\26\3\26"+
		"\3\26\3\27\5\27\u00de\n\27\3\27\3\27\3\30\3\30\3\31\3\31\2\2\32\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\2/\2\61\2\3\2\6\3\2\62;\4\2--//\3\2))\4\2C"+
		"\\c|\u00ea\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\3\63\3\2\2"+
		"\2\5:\3\2\2\2\7B\3\2\2\2\tH\3\2\2\2\13J\3\2\2\2\rL\3\2\2\2\17S\3\2\2\2"+
		"\21V\3\2\2\2\23_\3\2\2\2\25q\3\2\2\2\27}\3\2\2\2\31\u0086\3\2\2\2\33\u008f"+
		"\3\2\2\2\35\u009f\3\2\2\2\37\u00a1\3\2\2\2!\u00a4\3\2\2\2#\u00a7\3\2\2"+
		"\2%\u00ab\3\2\2\2\'\u00b6\3\2\2\2)\u00cf\3\2\2\2+\u00d3\3\2\2\2-\u00dd"+
		"\3\2\2\2/\u00e1\3\2\2\2\61\u00e3\3\2\2\2\63\64\7D\2\2\64\65\7G\2\2\65"+
		"\66\7I\2\2\66\67\7K\2\2\678\7P\2\289\7\"\2\29\4\3\2\2\2:;\7E\2\2;<\7Q"+
		"\2\2<=\7O\2\2=>\7O\2\2>?\7K\2\2?@\7V\2\2@A\7\"\2\2A\6\3\2\2\2BC\7\"\2"+
		"\2CD\7*\2\2DE\7c\2\2EF\7v\2\2FG\7\"\2\2G\b\3\2\2\2HI\7+\2\2I\n\3\2\2\2"+
		"JK\7\"\2\2K\f\3\2\2\2LM\7v\2\2MN\7c\2\2NO\7d\2\2OP\7n\2\2PQ\7g\2\2QR\7"+
		"\"\2\2R\16\3\2\2\2ST\7<\2\2TU\7\"\2\2U\20\3\2\2\2VW\7K\2\2WX\7P\2\2XY"+
		"\7U\2\2YZ\7G\2\2Z[\7T\2\2[\\\7V\2\2\\]\7<\2\2]^\7\"\2\2^\22\3\2\2\2_`"+
		"\7W\2\2`a\7R\2\2ab\7F\2\2bc\7C\2\2cd\7V\2\2de\7G\2\2ef\7<\2\2fg\7\"\2"+
		"\2gh\7q\2\2hi\7n\2\2ij\7f\2\2jk\7/\2\2kl\7m\2\2lm\7g\2\2mn\7{\2\2no\7"+
		"<\2\2op\7\"\2\2p\24\3\2\2\2qr\7p\2\2rs\7g\2\2st\7y\2\2tu\7/\2\2uv\7v\2"+
		"\2vw\7w\2\2wx\7r\2\2xy\7n\2\2yz\7g\2\2z{\7<\2\2{|\7\"\2\2|\26\3\2\2\2"+
		"}~\7W\2\2~\177\7R\2\2\177\u0080\7F\2\2\u0080\u0081\7C\2\2\u0081\u0082"+
		"\7V\2\2\u0082\u0083\7G\2\2\u0083\u0084\7<\2\2\u0084\u0085\7\"\2\2\u0085"+
		"\30\3\2\2\2\u0086\u0087\7F\2\2\u0087\u0088\7G\2\2\u0088\u0089\7N\2\2\u0089"+
		"\u008a\7G\2\2\u008a\u008b\7V\2\2\u008b\u008c\7G\2\2\u008c\u008d\7<\2\2"+
		"\u008d\u008e\7\"\2\2\u008e\32\3\2\2\2\u008f\u0090\7*\2\2\u0090\u0091\7"+
		"p\2\2\u0091\u0092\7q\2\2\u0092\u0093\7/\2\2\u0093\u0094\7v\2\2\u0094\u0095"+
		"\7w\2\2\u0095\u0096\7r\2\2\u0096\u0097\7n\2\2\u0097\u0098\7g\2\2\u0098"+
		"\u0099\7/\2\2\u0099\u009a\7f\2\2\u009a\u009b\7c\2\2\u009b\u009c\7v\2\2"+
		"\u009c\u009d\7c\2\2\u009d\u009e\7+\2\2\u009e\34\3\2\2\2\u009f\u00a0\7"+
		"]\2\2\u00a0\36\3\2\2\2\u00a1\u00a2\7_\2\2\u00a2\u00a3\7<\2\2\u00a3 \3"+
		"\2\2\2\u00a4\u00a5\7\60\2\2\u00a5\"\3\2\2\2\u00a6\u00a8\t\2\2\2\u00a7"+
		"\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2"+
		"\2\2\u00aa$\3\2\2\2\u00ab\u00ac\t\2\2\2\u00ac\u00ad\t\2\2\2\u00ad\u00ae"+
		"\t\2\2\2\u00ae\u00af\t\2\2\2\u00af\u00b0\7/\2\2\u00b0\u00b1\t\2\2\2\u00b1"+
		"\u00b2\t\2\2\2\u00b2\u00b3\7/\2\2\u00b3\u00b4\t\2\2\2\u00b4\u00b5\t\2"+
		"\2\2\u00b5&\3\2\2\2\u00b6\u00b7\t\2\2\2\u00b7\u00b8\t\2\2\2\u00b8\u00b9"+
		"\7<\2\2\u00b9\u00ba\t\2\2\2\u00ba\u00bb\t\2\2\2\u00bb\u00bc\7<\2\2\u00bc"+
		"\u00bd\t\2\2\2\u00bd\u00c4\t\2\2\2\u00be\u00c0\7\60\2\2\u00bf\u00c1\t"+
		"\2\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00be\3\2\2\2\u00c4\u00c5\3\2"+
		"\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\t\3\2\2\u00c7\u00c9\t\2\2\2\u00c8"+
		"\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2"+
		"\2\2\u00cb(\3\2\2\2\u00cc\u00d0\5/\30\2\u00cd\u00d0\5-\27\2\u00ce\u00d0"+
		"\5\61\31\2\u00cf\u00cc\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00ce\3\2\2\2"+
		"\u00d0\u00d1\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2*\3"+
		"\2\2\2\u00d3\u00d7\7)\2\2\u00d4\u00d6\n\4\2\2\u00d5\u00d4\3\2\2\2\u00d6"+
		"\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00da\3\2"+
		"\2\2\u00d9\u00d7\3\2\2\2\u00da\u00db\7)\2\2\u00db,\3\2\2\2\u00dc\u00de"+
		"\7/\2\2\u00dd\u00dc\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"\u00e0\t\2\2\2\u00e0.\3\2\2\2\u00e1\u00e2\t\5\2\2\u00e2\60\3\2\2\2\u00e3"+
		"\u00e4\7a\2\2\u00e4\62\3\2\2\2\13\2\u00a9\u00c2\u00c4\u00ca\u00cf\u00d1"+
		"\u00d7\u00dd\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}