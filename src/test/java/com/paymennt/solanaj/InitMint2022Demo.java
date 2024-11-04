package com.paymennt.solanaj;

import com.paymennt.crypto.lib.Base58;
import com.paymennt.solanaj.api.rpc.Cluster;
import com.paymennt.solanaj.api.rpc.SolanaRpcClient;
import com.paymennt.solanaj.data.SolanaAccount;
import com.paymennt.solanaj.data.SolanaTransaction;
import com.paymennt.solanaj.program.TokenProgram;

/**
 * demo for init mint
 * @author liuyu
 */
public class InitMint2022Demo {
    public static void main(String[] args) {
        SolanaRpcClient client = new SolanaRpcClient(Cluster.DEVNET,"127.0.0.1",1080);

        //payer of issue
        SolanaAccount payer = null;

        System.out.println("payer==>" + payer.getPublicKey());

        SolanaAccount mint = new SolanaAccount();
        System.out.println("mint.priKey==>" + Base58.encode(mint.getSecretKey()));
        System.out.println("mint.pubKey==>" + mint.getPublicKey());

        SolanaAccount closeAuthority = new SolanaAccount();
        System.out.println("closeAuthority.priKey==>" + Base58.encode(closeAuthority.getSecretKey()));
        System.out.println("closeAuthority.pubKey==>" + closeAuthority.getPublicKey());

        SolanaAccount mintAuthority = new SolanaAccount();
        System.out.println("mintAuthority.priKey==>" + Base58.encode(mintAuthority.getSecretKey()));
        System.out.println("mintAuthority.pubKey==>" + mintAuthority.getPublicKey());

        SolanaAccount freezeAuthority = new SolanaAccount();
        System.out.println("freezeAuthority.priKey==>" + Base58.encode(freezeAuthority.getSecretKey()));
        System.out.println("freezeAuthority.pubKey==>" + freezeAuthority.getPublicKey());
        //Byte length of a mint
        long space = 82;
        //get minimum balance for rent exemption
        long lamports = client.getApi().getMinimumBalanceForRentExemption(space);
        System.out.println("lamports==>" + lamports);
        //Number of decimals in token account amounts
        long tokenDecimal = 9;
        //last block hash
        String lastBloackHash = client.getApi().getLatestBlockhash();
        //tx for init mint
        SolanaTransaction tx = TokenProgram.createMintTx2022(payer,mint,mintAuthority,freezeAuthority,space,lamports,tokenDecimal,lastBloackHash);
        //send tx
        String txId = client.getApi().sendTransaction(tx);
        System.out.println("txId==>" + txId);
    }
}
