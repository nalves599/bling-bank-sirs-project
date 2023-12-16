import { EMAIL_FROM } from '../config';
import mail from '../email';

export const sendSharedSecret = async (email: string, sharedSecret: string) => {
  const emailTemplate = `
    <h1>Bling Bank</h1>
    <h3>Thank you for registering!</h3>

    <p>Here is your shared secret: <strong>${sharedSecret}</strong></p>
    <p style="color: red;">
      WARNING: Do not share this secret and keep it safe!
    </p>
  `;

  const info = await mail.sendMail({
    from: EMAIL_FROM,
    to: email,
    subject: 'BlingBank - Thank you for registering',
    html: emailTemplate,
  });

  console.debug('Email sent to', email, 'with ID', info);
};
