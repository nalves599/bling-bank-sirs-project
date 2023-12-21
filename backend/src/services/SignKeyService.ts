import db from "../database";

import { crypto } from "blingbank-lib";

export const addKey = async (
  signKey: string,
  userId: string,
  key: ArrayBuffer,
) => {
  const hash = crypto.bufferToHex(await crypto.sha256(key));
  const content = String(await crypto.paramProtect(signKey, key));

  
  // Check if key already exists
  if (
    await db.signKey.findFirst({
      where: {
        hash,
      },
    })
    ) {
      return;
    }

  console.log("Add key", hash, content);
    
  return db.signKey.create({
    data: {
      content,
      hash,
      userId,
    },
  });
};

export const getUserKeys = async (userId: string, key: ArrayBuffer) => {
  const keys = await db.signKey.findMany({
    where: {
      userId,
    },
  });

  return await Promise.all(
    keys.map(async (signKey) => ({
      ...signKey,
      content: crypto.decoder.decode(
        await crypto.paramUnprotect(signKey.content, key),
      ),
    })),
  );
};
