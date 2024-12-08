package interpreter.evaluating;

import interpreter.Lox;
import interpreter.parser.Expression;
import lombok.NonNull;

public class Interpreter implements Expression.Visitor<Value> {

	private final Lox lox;

	public Interpreter(
		@NonNull Lox lox
	) {
		this.lox = lox;
	}

	public Value evaluate(Expression expression) {
		return visit(expression);
	}

	@Override
	public Value visitLiteral(Expression.Literal literal) {
		return literal.value().toValue();
	}

	@Override
	public Value visitGrouping(Expression.Grouping grouping) {
		return evaluate(grouping.expression());
	}

	@Override
	public Value visitUnary(Expression.Unary unary) {
		final var right = evaluate(unary.right());

		return switch (unary.operator().type()) {
			case BANG -> new Value.Boolean(!isTruthy(right));
			case MINUS -> {
				if (right instanceof Value.Number(final var value)) {
					yield new Value.Number(-value);
				}

				throw new UnsupportedOperationException();
			}
			default -> throw new UnsupportedOperationException();
		};
	}

	@Override
	public Value visitBinary(Expression.Binary binary) {
		throw new UnsupportedOperationException();
	}

	public boolean isTruthy(Value value) {
		return switch (value) {
			case Value.Nil __ -> false;
			case Value.Boolean(final var rawValue) -> rawValue;
			default -> true;
		};
	}

}