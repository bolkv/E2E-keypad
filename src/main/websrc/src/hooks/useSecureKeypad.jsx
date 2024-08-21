import { useState, useEffect } from 'react';
import axios from 'axios';
import JSEncrypt from 'jsencrypt';

export default function useSecureKeypad() {
    const [keypad, setKeypad] = useState(null);
    const [userInput, setUserInput] = useState([]);
    const [hashes, setHashes] = useState([]);
    const [uuid, setUuid] = useState('');
    const [keypadhash, setKeypadhash] = useState('');
    const [timestamp, setTimestamp] = useState('');
    const [shuffledIndex, setShuffledIndex] = useState([]);  // 섞인 index 배열
    const publicKey = "-----BEGIN PUBLIC KEY-----\n" +
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkLA7dcyLqz4M6BS/XZi\n" +
        "wMee85fjwskmxfZVN/qI854Sa4mlU/5Rse0HcNY0QoF+J3kQF3xWpTKLfw2p5pzt\n" +
        "sALLN6gsO2m4qLIOk3eNR+hVL2Rh4dc8MAhuXfoTGrfMjXouiy05rYgVpqIRRCjz\n" +
        "MVGYnJ7arZ6rMN73nRxd0I9RVbe3LXEuHrBysxjfXae6z+qb+1Rp9MKnwiDuKC/i\n" +
        "2lqqqmV9p/8OuY+qUzsMCtU8URS8kvw/bkg90TEOHzjKWrRIYRcQQkdJ8KuX3/lV\n" +
        "1jBBgIQRfmQVTFUnkV5XBZw9jXYTsz6Bcp4MNWUlwHQIebAM8vMZ6/nH9p4OdETA\n" +
        "5wIDAQAB\n" +
        "-----END PUBLIC KEY-----\n"

    useEffect(() => {
        if (userInput.length === 6) {
            const inputString = userInput.join('');
            const encryptedString = encryptData(inputString);

            if (encryptedString) {
                const requestData = {
                    uuid: uuid,
                    timestamp: timestamp,
                    keypadhash: keypadhash,
                    input: encryptedString // input 필드에 암호화된 문자열을 할당
                };

                postInput(requestData).then(() => {
                    setUserInput([]);  // 입력 초기화
                    window.location.reload();  // 서버 응답 후 페이지 새로고침
                });
            }
        }
    }, [userInput]);

    const getSecureKeypad = async () => {
        return await axios.get('http://localhost:8080/api/keypad')
            .then(response => {
                const { image, uuid, timestamp, hashes, keypadhash, shuffledIndex } = response.data;
                console.log("Fetched Keypad Data:", response.data); // 셔플된 데이터를 확인
                setKeypad(image);
                setHashes(hashes);
                setUuid(uuid);
                setTimestamp(timestamp);
                setKeypadhash(keypadhash);
                setShuffledIndex(shuffledIndex);
            })
            .catch(error => {
                console.error('Error fetching keypad image:', error);
                throw error;
            });
    };

    const postInput = async (data) => {
        try {
            const response = await axios.post('http://localhost:8080/api/input', data);
            alert(`서버 응답: ${response.data}`);
        } catch (error) {
            console.error('Error saving input:', error);
        }
    };

    const onKeyPressed = (index) => {
        const hashValue = hashes[index];
        setUserInput(prevInput => {
            if (prevInput.length >= 6) return prevInput;
            return [...prevInput, hashValue];
        });
    };

    const encryptData = (data) => {
        if (!publicKey) {
            console.error('Public key is not set');
            return null;
        }
        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(publicKey); // 서버에서 받은 공개 키 사용
        const encryptedData = encryptor.encrypt(data);
        console.log('Fetched public key:', publicKey); // 공개 키를 콘솔에 출력
        if (!encryptedData) {
            console.error('Encryption failed');
        }
        return encryptedData;
    };

    return {
        states: {
            keypad,
            userInput: userInput.join(','),
        },
        actions: {
            getSecureKeypad,
            onKeyPressed,
        },
    };
}
