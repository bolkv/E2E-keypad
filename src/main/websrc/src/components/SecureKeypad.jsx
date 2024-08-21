import '../style/keypad.css';
import useSecureKeypad from "@/hooks/useSecureKeypad.jsx";
import { useEffect } from 'react';

export default function SecureKeypad({ keypad, onKeyPressed }) {
    const positions = [
        { x: 0, y: 0 }, { x: -50, y: 0 }, { x: -100, y: 0 }, { x: -150, y: 0 },
        { x: 0, y: -50 }, { x: -50, y: -50 }, { x: -100, y: -50 }, { x: -150, y: -50 },
        { x: 0, y: -100 }, { x: -50, y: -100 }, { x: -100, y: -100 }, { x: -150, y: -100 }
    ];
    const buttonSize = 50;
    const imageSize = { width: 200, height: 150 };

    if (!keypad) {
        return <div>Loading...</div>;  // 키패드 이미지를 불러오는 동안 로딩 메시지 표시
    }

    return (
        <table className="table-style">
            <tbody>
            {[0, 1, 2].map(row => (
                <tr key={row}>
                    {[0, 1, 2, 3].map(col => {
                        const index = row * 4 + col;
                        const pos = positions[index];
                        return (
                            <td key={col}>
                                <button
                                    type="button"
                                    className="key"
                                    onClick={() => onKeyPressed(index)}
                                    style={{
                                        backgroundImage: `url(data:image/png;base64,${keypad})`,
                                        backgroundPosition: `${pos.x}px ${pos.y}px`,
                                        width: `${buttonSize}px`,
                                        height: `${buttonSize}px`,
                                        backgroundSize: `${imageSize.width}px ${imageSize.height}px`,
                                        borderRadius: '50%',
                                        border: 'none',
                                        padding: 0,
                                        margin: 0,
                                    }}
                                >
                                    <span className={`number key${index + 1}`} />
                                </button>
                            </td>
                        );
                    })}
                </tr>
            ))}
            </tbody>
        </table>
    );
}