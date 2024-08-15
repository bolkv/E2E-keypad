import '../style/keypad.css';

export default function KeypadUserInput({ userInput }) {
    // 최대 6자리 입력을 허용하고, 각 해시 값에 대해 * 하나로 표시
    const maxLength = 6;

    // userInput이 비어 있는 경우 빈 문자열 반환
    if (!userInput) {
        return <div className="input-group-style"></div>;
    }

    const maskedInput = userInput
        .split(',')
        .slice(0, maxLength)  // 최대 6자리까지 표시
        .map(() => '*')
        .join('');

    return (
        <div className="input-group-style">
            {maskedInput.split('').map((char, index) => (
                <span key={index} className="input-char">
                    {char}
                </span>
            ))}
        </div>
    );
}
